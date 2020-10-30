package com.neco.common.web;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author ziyuan_deng
 * @date 2020/9/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SgUser implements Serializable {

    private String id;
    private String username;
    private String password;
    private String company_id;
    private String name;
    private String utype;
    private String birthday;
    private String userpic;
    private String sex;
    private String email;
    private String phone;
    private String status;
    private Date createTime;
    private Date updateTime;

    private List<String> permissions;
}
