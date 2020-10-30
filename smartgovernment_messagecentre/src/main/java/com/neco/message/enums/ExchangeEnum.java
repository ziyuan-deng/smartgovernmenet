package com.neco.message.enums;

import com.neco.message.constant.ExchangeType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 交换机枚举，要添加在此动态添加
 * @author ziyuan_deng
 * @date 2020/9/16
 */
@Getter
public enum ExchangeEnum {
    //fanout交换机设置区域
    FANOUT("com.rabbit.message.fonout", ExchangeType.FANOUTTYPE,true,false),


    //direct交换机设置区域
    DIRECT("com.rabbit.message.direct", ExchangeType.DIRECTTYPE,true,false),


    //topic交换机设置区域
    TOPIC("com.rabbit.message.topic", ExchangeType.TOPICTYPE,true,false);



    //headers交换机类型设置区域
   // HEADERS("com.rabbit.message.headers", ExchangeType.HEADERS,true);

    private ExchangeEnum( String exchangeName,Integer exchangeType,boolean durable,boolean autoDelete){
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        this.durable = durable;
        this.autoDelete = autoDelete;
    }
    //交换机名称
    private String exchangeName;
    //交换机类型
    private int exchangeType;
    //是否持久化
    private boolean durable;

    private boolean autoDelete;

    public static  List<ExchangeEnum> toList(){
        List<ExchangeEnum> list = new ArrayList<>(ExchangeEnum.values().length);
        for (ExchangeEnum value : ExchangeEnum.values()) {
            list.add(value);
        }
        return list;
    }
}
