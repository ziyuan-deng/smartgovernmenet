package com.neco.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neco.message.service.MessageService;
import com.neco.messagecentre.dto.MessageDto;
import com.neco.messagecentre.model.MessageInfo;
import com.neco.sglog.annotation.SgLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

/**
 * 消息发送
 * @author ziyuan_deng
 * @date 2020/9/14
 */

@RestController
@RequestMapping("/mesage")
@Slf4j

public class MessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageService messageService;

    @GetMapping("/send")
    @SgLog(serverName = "smartgovernment_messagecentre",description = "消息发送")
    public String sendMessage(String exchangeName,String routingKey,String message){
        try {
           // rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            /*rabbitTemplate.setExchange(exchangeName);
            rabbitTemplate.setRoutingKey(routingKey);
            rabbitTemplate.convertAndSend(message);
            Person person = new Person(UUID.randomUUID().toString(),"马拉多纳",58);
            String userStr = objectMapper.writeValueAsString(person);*/
            String str = "{\"id\":\"2a46de65-03f9-4558-b659-d4db84fc6414\",\"name\":\"马拉多纳\",\"age\":58}";

            CorrelationData correlationData = new CorrelationData();
            String msgId = UUID.randomUUID().toString();

            correlationData.setId(msgId);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationId(msgId);
            Message msg = new Message(str.getBytes(),messageProperties);
            rabbitTemplate.convertAndSend(exchangeName,routingKey,msg,correlationData);

        }catch (Exception ex){
            log.error("消息发送失败！",ex);
        }
       return "success";
    }

    @PostMapping("/sendObj")
    @SgLog(serverName = "smartgovernment_messagecentre",description = "消息发送")
    public String sendMessageByObj(@RequestBody MessageDto message){

        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        try {
            Object params = message.getParams();
            String userStr = objectMapper.writeValueAsString(params);
            CorrelationData correlationData = new CorrelationData();
            String msgId = UUID.randomUUID().toString();
            String msgLastId = null;
            MessageProperties messageProperties = new MessageProperties();
            if (message.isDurable()){
                MessageInfo messageInfo = new MessageInfo(msgId, userStr, "0", new Date());
                messageService.insert(messageInfo);
                msgLastId = msgId + "@@true";
                log.info("持久化的Id:" + msgLastId);
                correlationData.setId(msgLastId);
                messageProperties.setCorrelationId(msgLastId);
            }else{
                correlationData.setId(msgId);
                messageProperties.setCorrelationId(msgId);
            }
            Message msg = new Message(userStr.getBytes(),messageProperties);

            rabbitTemplate.convertAndSend(message.getExchangeName(),message.getRoutingKey(),msg,correlationData);
        }catch (Exception ex){
            log.error("消息发送失败！",ex);
        }
        return "success";
    }
}
