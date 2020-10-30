package com.neco.messagecentre.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 测试用
 * @author ziyuan_deng
 * @date 2020/9/17
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

    private String id;

    private String name;

    private Integer age;

}
