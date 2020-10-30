package com.neco.sglog.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author ziyuan_deng
 * @date 2020/9/9
 */
@Data
@ToString
@Document("doc_zxs_systemloginfo")
public class RequestLogInfo implements Serializable {

    @Id
    private String logId;
    //用户ID
    private String userId;
    //用户名
    private String userName;
    //单位id
    private String unitId;
    //单位名称
    private String unitName;

    private String microservice;
    //发送请求ip
    private String fromIp;
    //发送到的IP
    private String toIp;
    //访问资源
    private String visitResource;
    //描述信息
    private String description;
    //访问的时间
    private String visitTime;
    //提交请求参数
    private String submitParam;
    //返回请求参数
    private String returnParam;
    //请求耗时
    private String timeConsume;
    //请求结果
    private String result;

}
