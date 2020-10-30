package com.neco.auth.service;


import com.neco.authcentre.model.AuthToken;

public interface AuthService {

    /**
     * 认证方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret);

    /**
     * 获取jwt_token
     * @param access_token
     * @return
     */
    AuthToken getUserToken(String access_token);

    void delToken(String uid);

    AuthToken applyRefrshToken(String refreshToken, String clientId, String clientSecret);
}
