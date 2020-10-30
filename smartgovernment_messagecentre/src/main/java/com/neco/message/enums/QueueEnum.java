package com.neco.message.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 队列枚举，动态添加在此添加
 * @author ziyuan_deng
 * @date 2020/9/16
 */
@Getter
public enum QueueEnum {
    //fonout队列类型设置区域
    FONOUT_TEST("com.test.fonout.queue","test.fonout.*",true,false,false,null,false,null, ExchangeEnum.FANOUT),


    //direct队列类型设置区域
    DIRECT_TEST("com.test.direct.queue","test.direct.key",true,false,false,null,false,null,ExchangeEnum.DIRECT),

    //topic队列类型设置区域
    TOPIC_TEST("com.test.topic.queue","test.topic.*",true,false,false,null,false,null,ExchangeEnum.TOPIC);


    //headers队列类型设置区域
   // HEADERS_TEST("com.test.header.queue","test.fonout.*",true,true,false,null,false,null,ExchangeEnum.HEADERS);

    QueueEnum(String queueName, String routingKey, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments, boolean whereAll, Map<String, Object> headers, ExchangeEnum exchangeEnum) {
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
        this.arguments = arguments;
        this.whereAll = whereAll;
        this.headers = headers;
        this.exchangeEnum = exchangeEnum;
    }

    //队列名称
    private String queueName;
    //路由键
    private String routingKey;
    //队列是否持久化
    private boolean durable;

    //是否为排序队列
    private boolean exclusive;
    //队列为空时，是否自动删除队列
    private boolean autoDelete;
    //队列的参数
    private Map<String,Object> arguments;
    //是否需要全部匹配（用于头队列使用）
    private boolean whereAll;
    //匹配消息头（用于头队列使用）
    private Map<String,Object> headers;
    //交换机
    private ExchangeEnum exchangeEnum;

    public static List<QueueEnum> toList(){
        List<QueueEnum> enumList = new ArrayList<>(QueueEnum.values().length);
        QueueEnum[] values = QueueEnum.values();
        for (QueueEnum value : values) {
            enumList.add(value);
        }
        return enumList;
    }

}
