package com.neco.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neco.gateway.filter.response.ResponseDataUtils;
import com.neco.gateway.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;




/**
 * 微服务认证过滤器
 * @author ziyuan_deng
 * @date 2020/9/24
 */
@Component

public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthGlobalFilter.class);

    @Value("${filter.excludeUrl}")
    private String excludeUrl;

    @Value("${auth.tokenValiditySeconds}")
    long tokenValiditySeconds;

    private static final String AUTHORIZE_TOKEN = "Authorization";
    private static final String JTI_TOKEN = "uid";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String[] urls = excludeUrl.split(",");
        //如果是登录或者开放的的URL，放行
        for (String url : urls) {
            String path = request.getURI().getPath();
            if (request.getURI().getPath().indexOf(url)!=-1) {
                return chain.filter(exchange);
            }
        }
        //获取请求头令牌
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        //请求头为空，则从cookie获取
        /*if (StringUtils.isBlank(token)) {
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie!=null){
                token = cookie.getValue();
                if (StringUtils.isNotBlank(token)) {
                    //将token添加到请求头上
                    request.mutate().header(AUTHORIZE_TOKEN,"Bearer " + token);
                }
            }
        }*/
        //请求参数获取
        if (StringUtils.isBlank(token)) {
             token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            if (StringUtils.isNotBlank(token)) {
                request.mutate().header(AUTHORIZE_TOKEN,"Bearer " + token);
            }
        }
        //如果token为空，意味着没有认证，因此结束访问
        if (StringUtils.isBlank(token)) {
           return authError(response,HttpStatus.UNAUTHORIZED.value(),"没有登录，请先登录再进行别的操作！");
        }
        if (StringUtils.isNotBlank(token)) {
            String uid = request.getHeaders().getFirst(JTI_TOKEN);
            String tokenKey = "user_token:" + uid;
            boolean exists = redisUtil.exists(tokenKey);
            if (exists) {
                Object tokenStr = redisUtil.get(tokenKey);
                redisUtil.set(tokenKey,tokenStr,tokenValiditySeconds);
            }else {
                return authError(response,-201,"令牌过期，请重新登陆！");
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 检验不通过，要把检验不通过信息传给前端
     * @param resp
     * @param mess
     * @return
     */
    private Mono<Void> authError(ServerHttpResponse resp,int code,String mess) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        ModelMap modelMap = ResponseDataUtils.retErrorInfo(code, mess);
        String returnStr = "";
        try {
            returnStr = objectMapper.writeValueAsString(modelMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer buffer = resp.bufferFactory().wrap(returnStr.getBytes());
        return resp.writeWith(Mono.just(buffer));
    }



    @Override
    public int getOrder() {
        return 0;
    }
}
