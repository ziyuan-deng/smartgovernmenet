package com.neco.utils;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

public class ServletResponseHelper {

	public static HttpServletResponse getResponse(){
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        Assert.notNull(response, "获取请求对象失败");
        return response;
    }
}
