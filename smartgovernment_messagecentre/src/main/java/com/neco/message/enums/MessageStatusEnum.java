package com.neco.message.enums;

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum MessageStatusEnum {
    NOTSEND("0","未发送"),
    SENDED("1","发送成功"),
    COMSUMERED("3","已消费");


    private MessageStatusEnum(String code, String msgContent) {
        this.code = code;
        this.msgContent = msgContent;
    }

    private String code;

    private String msgContent;
}
