package com.neco.auth.controller;


import com.neco.auth.service.AuthService;
import com.neco.authcentre.dto.LoginRequest;
import com.neco.authcentre.model.AuthToken;
import com.neco.common.enums.CodeEnum;
import com.neco.common.response.ResponseUtil;
import com.neco.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 登陆接口
 *
 * @author ziyuan_deng
 * @create 2020-08-14 1:39
 */
@RestController
@Slf4j
public class AuthController  {

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    long tokenValiditySeconds;
    @Resource
    AuthService authService;

    @PostMapping("/userlogin")
    public ModelMap login(LoginRequest loginRequest) {
        //校验账号是否输入
        if(loginRequest == null || StringUtils.isBlank(loginRequest.getUsername())){
            return ResponseUtil.retErrorInfo(CodeEnum.AUTH_USERNAME_NONE.getCode(),CodeEnum.AUTH_USERNAME_NONE.getMsg());
        }
        //校验密码是否输入
        if(StringUtils.isBlank(loginRequest.getPassword())){
            return ResponseUtil.retErrorInfo(CodeEnum.AUTH_PASSWORD_NONE.getCode(),CodeEnum.AUTH_PASSWORD_NONE.getMsg());
        }
        AuthToken authToken = null;
        authToken = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), clientId, clientSecret);

        return ResponseUtil.retCorrectModel(authToken);
    }



    @GetMapping("/userjwt")
    public ModelMap userjwt() {
        //获取cookie中的令牌
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uid = getTokenFormHeader(request);
        //根据令牌从redis查询jwt
        AuthToken authToken = authService.getUserToken(uid);
        if(authToken == null){
            return ResponseUtil.retErrorInfo("获取令牌操作失败！");
        }
        return ResponseUtil.retCorrectModel(authToken.getAccess_token());
    }

    /**
     * 退出系统
     * @return
     */
    @PostMapping("/userlogout")
    public ModelMap logout() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //取出身份令牌
        String uid = getTokenFormHeader(request);
        //删除redis中token
        authService.delToken(uid);
        //删除JWTtoken
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null);
        //清除cookie
       // clearCookie(uid);
        return ResponseUtil.retCorrectInfo("操作成功！");
    }

    private String getTokenFormHeader(HttpServletRequest request) {
        String header = request.getHeader("uid");
        return header;
    }

    /**
     * token过期，需要刷新token获取新的token
     * @param refreshToken
     * @return
     */
    @PostMapping("/refreshtoken")
    public ModelMap refreshToken(String refreshToken){
        if (StringUtils.isBlank(refreshToken)) {
            return ResponseUtil.retErrorInfo(CodeEnum.AUTH_LOGIN_REFRESHTOKEN_NONE.getCode(),CodeEnum.AUTH_LOGIN_REFRESHTOKEN_NONE.getMsg());
        }
        AuthToken authToken = authService.applyRefrshToken(refreshToken,clientId, clientSecret);
        return ResponseUtil.retCorrectModel(authToken);
    }

    /**
     * 清除cookie
      * @param uid  身份id
     */
/*    private void clearCookie(String uid) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", uid, 0, false);
    }*/

    /**
     * 通过身份id获取token
     * @return
     */
 /*   private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        String accessToken = cookieMap.get("uid");
        return accessToken;
    }*/

  /* *//**
     * 获取个人身份token
     * @param access_token
     *//*
    private void saveCookie(String access_token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //添加cookie 认证令牌，最后一个参数设置为false，表示允许浏览器获取
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", access_token, cookieMaxAge, false);
    }*/
}
