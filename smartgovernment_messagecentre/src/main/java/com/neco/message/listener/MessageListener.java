package com.neco.message.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neco.message.constant.MessageCentreConstant;
import com.neco.message.enums.MessageStatusEnum;
import com.neco.message.service.MessageService;
import com.neco.messagecentre.model.MessageInfo;
import com.neco.messagecentre.model.Person;
import com.neco.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 消息监听器
 * @author ziyuan_deng
 * @date 2020/9/14
 */
@Component
@Slf4j
public class MessageListener  {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MessageService messageService;
    @RabbitListener(queues = "com.test.direct.queue",containerFactory = "singleListenerContainer")
    public void handlerMessage(Message message){
        byte[] body = message.getBody();
        String correlationId = message.getMessageProperties().getCorrelationId();
        try {
            Person person = objectMapper.readValue(body, Person.class);
            //幂等性处理
            if (StringUtils.isNotBlank(correlationId)){
                Set<Object> msgIdSet = redisUtil.sGet(MessageCentreConstant.MESSAGEID_SET);
                if (msgIdSet==null){
                    //处理有关业务逻辑

                    log.info("接收到消息并要处理消息："+ person.toString()+"********消息id："+ correlationId);
                    redisUtil.sSetAndTime(MessageCentreConstant.MESSAGEID_SET,MessageCentreConstant.DELAY_TIME, correlationId);
                    updateMessageStatus(correlationId);
                } else if ( !msgIdSet.contains(correlationId)) {
                    //处理有关业务逻辑

                    log.info("接收到消息并要处理消息："+ person.toString()+"********消息id："+ correlationId);
                    redisUtil.sSetAndTime(MessageCentreConstant.MESSAGEID_SET,MessageCentreConstant.DELAY_TIME, correlationId);
                    updateMessageStatus(correlationId);
                }else {
                    //不处理

                    log.info("消息已经处理过："+ person.toString()+"********消息id："+ correlationId);
                }
            }
        }catch (Exception ex){
            log.error("消息（id: "+correlationId+"+）处理异常：",ex.getMessage());
        }
    }

    @RabbitListener(queues = "com.test.topic.queue",containerFactory = "singleListenerContainer")
    public void handlerTopicMessage(Message message){
        byte[] body = message.getBody();
        try {
            String resultMsg = objectMapper.readValue(body, String.class);
            log.info("收到信息***********：" + resultMsg );
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 消息消费后更新状态
     * @param correlationId
     */
    private void updateMessageStatus(String correlationId) {
        MessageInfo messageInfo = new MessageInfo();
        String[] idStrs = correlationId.split("@@");
        messageInfo.setId(idStrs[0]);
        messageInfo.setStatus(MessageStatusEnum.COMSUMERED.getCode());
        messageService.updateById(messageInfo);
    }
}
