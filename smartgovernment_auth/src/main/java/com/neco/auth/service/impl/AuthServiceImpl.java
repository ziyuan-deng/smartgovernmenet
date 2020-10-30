package com.neco.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.neco.auth.service.AuthService;
import com.neco.authcentre.model.AuthToken;
import com.neco.authcentre.response.AuthCode;
import com.neco.common.exception.ExceptionCast;
import com.neco.common.microservice.SgServiceList;
import com.neco.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * 认证处理类
 *
 * @author ziyuan_deng
 * @create 2020-08-14 1:07
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Value("${auth.tokenValiditySeconds}")
    long tokenValiditySeconds;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RedisUtil redisUtil;

    //认证方法
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret){

        //申请令牌
        AuthToken authToken = applyToken(username,password,clientId, clientSecret);
        if(authToken == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //将 token存储到redis
        String access_token = authToken.getJti_token();
        String content = JSON.toJSONString(authToken);
        boolean saveTokenResult = saveToken(access_token, content, tokenValiditySeconds);
        if(!saveTokenResult){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    /**
     * 获取jwt_token
     * @param uid  身份令牌
     * @return
     */
    @Override
    public AuthToken getUserToken(String uid) {
        String userToken = "user_token:"+uid;
        Object token = redisUtil.get(userToken);
        if (token != null) {
            AuthToken authToken = null;
            try {
                authToken = JSON.parseObject(token.toString(), AuthToken.class);
            } catch (Exception e) {
                LOGGER.error("getUserToken from redis and execute JSON.parseObject error {}",e.getMessage());
                e.printStackTrace();
            }
            return authToken;
        }
        return null;
    }

    @Override
    public void delToken(String uid) {
        String name = "user_token:" + uid;
        redisUtil.remove(name);
    }

    /**
     * 令牌过期通过刷新令牌获取新的令牌
     * @param refreshToken
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken applyRefrshToken(String refreshToken, String clientId, String clientSecret) {
        //采用客户端负载均衡，从eureka获取认证服务的ip 和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(SgServiceList.SMARTGOVERNMENT_AUTH);
        URI uri = serviceInstance.getUri();
        String refreshUrl = uri+"/auth/oauth/token";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String httpbasic = httpbasic(clientId, clientSecret);
        headers.add("Authorization", httpbasic);
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","refresh_token");
        body.add("refresh_token",refreshToken);
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);

        Map tokenMap = null;
        try{
            //远程调用申请令牌
            ResponseEntity<Map> exchange = restTemplate.exchange(refreshUrl, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
            tokenMap = exchange.getBody();
        }catch (Exception ex){
            LOGGER.error("request oauth_token_password error: {}",ex.getMessage());
            ex.printStackTrace();
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYREFRESHTOKEN_FAIL);
        }
        if(tokenMap == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYREFRESHTOKEN_FAIL);
        }
        if(tokenMap.get("access_token") == null ||
                tokenMap.get("refresh_token") == null ||
                tokenMap.get("jti") == null){//jti是jwt令牌的唯一标识作为用户身份令牌
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYREFRESHTOKEN_FAIL);
        }
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) tokenMap.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) tokenMap.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) tokenMap.get("jti");
        authToken.setAccess_token(jwt_token);
        authToken.setJti_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    /**
     * 远程调用获取认证token
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //采用客户端负载均衡，从eureka获取认证服务的ip 和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(SgServiceList.SMARTGOVERNMENT_AUTH);
        URI uri = serviceInstance.getUri();
        String authUrl = uri+"/auth/oauth/token";
        //URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType
        // url就是 申请令牌的url /oauth/token
        //method http的方法类型
        //requestEntity请求内容
        //responseType，将响应的结果生成的类型
        //请求的内容分两部分
        //1、header信息，包括了http basic认证信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String httpbasic = httpbasic(clientId, clientSecret);
        headers.add("Authorization", httpbasic);
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        Map tokenMap = null;
        try{
            //远程调用申请令牌
            ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
            tokenMap = exchange.getBody();
        }catch (Exception ex){
            LOGGER.error("request oauth_token_password error: {}",ex.getMessage());
            ex.printStackTrace();
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        if(tokenMap == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        if(tokenMap.get("access_token") == null ||
            tokenMap.get("refresh_token") == null ||
            tokenMap.get("jti") == null){//jti是jwt令牌的唯一标识作为用户身份令牌
            String error_description = null;
            if (tokenMap.get("error_description") != null) {
                error_description = (String) tokenMap.get("error_description");
            }
            if(StringUtils.isNotBlank(error_description)){
                if(error_description.equals("坏的凭证")){
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }else if(error_description.indexOf("UserDetailsService returned null")>=0){
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }else if(error_description.equals("用户名或密码错误")){
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }

        }
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) tokenMap.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) tokenMap.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) tokenMap.get("jti");
        authToken.setAccess_token(jwt_token);
        authToken.setJti_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    /**
     * 令牌存入redis
     * @param access_token
     * @param content
     * @param tokenValiditySeconds
     * @return
     */
    private boolean saveToken(String access_token, String content, long tokenValiditySeconds) {
        ////将令牌写入redis,key为身份令牌user_token:+jti_token，value为AuthToken的JSON对象
        String name = "user_token:" + access_token;
        boolean flag = redisUtil.set(name, content, tokenValiditySeconds);
        return flag;
    }

    /**
     * 对客户端和密钥进行base64编码
     * @param clientId  客户端id
     * @param clientSecret　客户端密钥
     * @return
     */
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
}
