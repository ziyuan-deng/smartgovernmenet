package com.neco.sglog.listener;


import com.neco.sglog.event.RequestLogEvent;
import com.neco.sglog.model.RequestLogInfo;
import com.neco.sglog.service.RemoteLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 日志消息的监听器
 * @author ziyuan_deng
 * @date 2020/9/10
 */
public class RequestLogListener {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogListener.class);
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 请求日志的持久化
     * @param event
     */
    @Async
    @Order
    @EventListener
    public void saveSystemLog(RequestLogEvent event) {
        RequestLogInfo sysLog = (RequestLogInfo) event.getSource();
        logger.info("日志信息:"+sysLog.toString());
        remoteLogService.saveLog(sysLog);
    }

    public RemoteLogService getRemoteLogService() {
        return remoteLogService;
    }

    public void setRemoteLogService(RemoteLogService remoteLogService) {
        this.remoteLogService = remoteLogService;
    }

    public RequestLogListener(RemoteLogService remoteLogService) {
        this.remoteLogService = remoteLogService;
    }
}

