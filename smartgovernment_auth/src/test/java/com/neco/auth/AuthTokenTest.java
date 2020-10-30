package com.neco.auth;

import com.alibaba.fastjson.JSON;
import com.sun.jersey.core.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan_deng
 * @date 2020/9/22
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthTokenTest {

    @Test
    public void testCreateJwt(){
        //证书文件
        String key_location = "sg.keystore";
        //密钥库密码
        String keystore_password = "smartgovernmentkeystore";
        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,
                keystore_password.toCharArray());
        //密钥的密码，此密码和别名要匹配
        String keypassword = "smartgovernment";
        //密钥别名
        String alias = "sgkey";
        //密钥对（密钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypassword.toCharArray());
        //私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "mrt");
        tokenMap.put("roles", "r01,r02");
        tokenMap.put("ext", "1");
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("token="+token);
    }

    //资源服务使用公钥验证jwt的合法性，并对jwt解码
    @Test
    public void testVerify(){
        //jwt令牌
        String token ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHQiOiIxIiwicm9sZXMiOiJyMDEscjAyIiwibmFtZSI6Im1ydCIsImlkIjoiMTIzIn0.DqVTP1B6E3i1W18hh0KO4sKS21b9o8Jdxdz7U0Llv-rNI2GrKMvIeIlZBZfPjRjkF6Tlbis2wC69knOiL5U7TGTkjJW3stjNBTexiMEvfAstTC1AGI3rvXTHaHnXz3lHVCEMIYn6pR-0aOcmTcS5vRSifJK0jDEJVWZRr_SAth7-0FqlyAHitYBUOd5Mkz3GX4slTeX3w4KYxDj9i1Py3kcMaAocRSdQsrRO2IgFDt3zexwldnqy2Y2A3j97n5fJSv6lsBzipoehShtWH5IWyGKp3rf2WP-Ka_HZ_OOfq86rmokca81-XEkhL4hOwS9MAV--MDOkLj7vezKB_yKN4Q";
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtqpLhClm4dy8vVPYV7+LoBbIoD5ZtXywQRwfvjto3k9lg9XL/r5A1bWnbYyVvc8GdfVXY0ciGn3bzPW/AiiC8ck+/VDgJN0VHMfkDzRWl7sJc0eoVvKTXnqBK/9MvjM4umDitjogy7duMmVfJ1lZxxh2/AusmXwwo2aVMTm58Lg/MyuSDMjSWKw8BsvhSMNY2nddC6hDArJEAAbM0CZu0LErU4bvwzSjevLuvTgl8wJx0WDigD896MrwLJE2CITkcoa0K8TgJ6B5U+tQPAk/xJ1QNyGyMlD/R6MHZzqDQE/9N/g4wB75ijSfg1jPvp5OtpOvphUdKuCpKn/w6GHeZQIDAQAB-----END PUBLIC KEY-----";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
        //获取jwt原始内容
        String claims = jwt.getClaims();
        Map map = JSON.parseObject(claims, Map.class);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

    /**
     * 客户端密钥和用户密码加密
     */
    @Test
    public void testClient(){
        /*String clientStr = "SmartGovernment:SmartGovernment";
        byte[] encode = Base64.encode(clientStr.getBytes());
        String secret = new String(encode);
        System.out.println("客户端密钥clientsecret:"+ secret);*/
        String secret = "SmartGovernment";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode1 = bCryptPasswordEncoder.encode(secret);
        System.out.println("客户端密钥clientsecret22222:"+ encode1);

    }

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

   /* @Test
    public void testRemoteClient(){
        //采用客户端负载均衡，从eureka获取认证服务的ip 和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose("smartgovernment-auth");
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
        String httpbasic = httpbasic("XcWebApp", "XcWebApp");
        //"Basic WGNXZWJBcHA6WGNXZWJBcHA="
        headers.add("Authorization", httpbasic);
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new
                HttpEntity<MultiValueMap<String, String>>(body, headers);
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
        //远程调用申请令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST,
                multiValueMapHttpEntity, Map.class);
        Map body1 = exchange.getBody();
        System.out.println(body1);
    }*/

    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


}
