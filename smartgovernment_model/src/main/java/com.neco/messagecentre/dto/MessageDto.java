package com.neco.messagecentre.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ziyuan_deng
 * @date 2020/9/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
/*    {
            "exchangeName":"com.rabbit.message.direct",
            "routingKey":"test.direct.key",
            "params": {
                "id":"dsjgjalglag",
                "name":"马拉多纳",
                "age": 58
    }*/
    //交换机名称
    private String exchangeName;
   //路由键
    private String routingKey;
    //消息是否入库
    private boolean durable;
    //传递的对象值
    private Object params;

}
