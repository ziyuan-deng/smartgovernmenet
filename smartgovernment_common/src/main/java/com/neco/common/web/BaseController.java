package com.neco.common.web;

import com.alibaba.fastjson.JSON;
import com.neco.common.constants.CommonConstant;
import com.neco.common.exception.ExceptionCast;
import com.neco.common.response.responseEnums.CommonCode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 控制器基类
 * @author ziyuan_deng
 * @date 2020/10/14
 */
public class BaseController {

    //公钥
    private static final String PUBLIC_KEY = "publicKey.txt";

    /**
     * 获取登录用户信息
     * @param req
     * @return
     */
    public SgUser getCurrUser(HttpServletRequest req) {
        String token = req.getHeader(CommonConstant.LOGIN_TOKEN);
        SgUser aui = null;
        if (null != token) {
            String jwtToken = token.substring(7, token.length());
            Jwt jwt =JwtHelper.decodeAndVerify(jwtToken,new RsaVerifier(getPubKey()));
            //获取jwt原始内容
            String claims = jwt.getClaims();
            Map<String,String> map = JSON.parseObject(claims, Map.class);
            aui = SgUser.builder()
                    .id(map.get("id"))
                    .company_id(map.get("companyId"))
                    .name(map.get("name"))
                    .username(map.get("user_name"))
                    .permissions(Collections.singletonList(map.get("authorities")))
                    .build();
        } else {
            ExceptionCast.cast(CommonCode.PERMISSION_UNLOGIN);
        }

        return aui;
    }

    /**
     * 获取非对称加密公钥 Key
     * @return 公钥 Key
     */
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * 获取用户的登录令牌
     * @return
     */
    public String getLoginUserToken() {
        String token = getRequestHeader(CommonConstant.LOGIN_TOKEN);
        return token;
    }

    /**
     * 获取请求对象
     * @return
     */
    public  HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Assert.notNull(request, "获取请求对象失败");
        return request;
    }

    /**
     * 获取请求头
     * @param name 请求头名称
     * @return
     */
    public String getRequestHeader(String name){
        return getRequest().getHeader(name);
    }


}
