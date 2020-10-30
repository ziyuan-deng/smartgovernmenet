package com.neco.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * feign 拦截器实现微服务间认证
 * 微服务调用微服务之间，要将token传递过去，考虑到认证中心使用管理员token进行调用用户中心获取用户信息进行用户认证，
 * 因此每个为服务自己独立注入自己服务
 * @author ziyuan_deng
 * @date 2020/9/24
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZE_TOKEN = "Authorization";
    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request != null) {
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()){
                        String headerName = headerNames.nextElement();
                        if (headerName.equalsIgnoreCase(AUTHORIZE_TOKEN)){
                            String headerValue = request.getHeader(headerName);
                            requestTemplate.header(headerName,headerValue);
                        }
                    }
                }
            }
        }
    }
}
