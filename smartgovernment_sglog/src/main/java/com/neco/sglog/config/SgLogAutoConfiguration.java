package com.neco.sglog.config;

import com.neco.sglog.aop.LogAspect;
import com.neco.sglog.listener.RequestLogListener;
import com.neco.sglog.service.RemoteLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author ziyuan_deng
 * @date 2020/9/10
 */
@EnableAsync
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "enable",prefix = "sg.log",havingValue = "true",matchIfMissing = true)
@EnableMongoRepositories(basePackages = {"com.neco.sglog.dao"})
@Import(LogAspect.class)
public class SgLogAutoConfiguration {

    @Autowired
    private final RemoteLogService remoteLogService ;

    @Bean
    public RequestLogListener requestLogListener() {
        return new RequestLogListener(remoteLogService);
    }
    public SgLogAutoConfiguration(RemoteLogService remoteLogService) {
        this.remoteLogService = remoteLogService;
    }
}
