package com.neco.message.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听器
 *
 * @author ziyuan_deng
 * @create 2020-09-13 12:07
 */
@Component("simpleListener")
public class SimpleListener implements ChannelAwareMessageListener {

    private static final Logger log= LoggerFactory.getLogger(SimpleListener.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String v = objectMapper.readValue(message.getBody(), String.class);
        log.info("接受到的信息：{}",v);
    }
}

