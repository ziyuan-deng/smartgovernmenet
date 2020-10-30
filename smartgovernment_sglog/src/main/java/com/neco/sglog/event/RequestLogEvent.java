package com.neco.sglog.event;

import com.neco.sglog.model.RequestLogInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author ziyuan_deng
 * @date 2020/9/10
 */
public class RequestLogEvent extends ApplicationEvent {

    public RequestLogEvent(RequestLogInfo source) {
        super(source);
    }
}
