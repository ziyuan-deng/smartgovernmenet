package com.neco.sglog.service;

import com.neco.sglog.dao.RequestLogRepository;
import com.neco.sglog.model.RequestLogInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ziyuan_deng
 * @date 2020/9/16
 */
@Service
@Slf4j
public class RemoteLogService {
    @Autowired
    private RequestLogRepository logRepository;

    public boolean saveLog(RequestLogInfo sysLog) {
        RequestLogInfo info = logRepository.save(sysLog);
        log.info("保存的日志信息："+ sysLog.toString());
        return true;
    }
}
