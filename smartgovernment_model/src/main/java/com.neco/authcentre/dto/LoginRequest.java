package com.neco.authcentre.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Created by admin on 2018/3/5.
 */
@Data
@ToString
public class LoginRequest {

    String username;
    String password;
    String verifycode;

}
