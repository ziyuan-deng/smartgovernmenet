package com.neco.authcentre.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 令牌数据封装
 * @author ziyuan_deng
 * @date 2020/9/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthToken {

    String access_token;//jwt令牌
    String refresh_token;//刷新token
    String jti_token;//访问token
}
