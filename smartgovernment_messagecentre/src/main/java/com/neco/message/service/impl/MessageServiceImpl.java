package com.neco.message.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.neco.message.mapper.MessageMapper;
import com.neco.message.service.MessageService;
import com.neco.messagecentre.model.MessageInfo;
import org.springframework.stereotype.Service;

/**
 * @author ziyuan_deng
 * @date 2020/9/17
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageInfo> implements MessageService {
}
