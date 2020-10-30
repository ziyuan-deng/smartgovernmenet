package com.neco.filemanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ziyuan_deng
 * @create 2020-09-06 16:21
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.neco.sglog"})
@ComponentScan(basePackages = {"com.neco.common"})
@ComponentScan(basePackages = {"com.neco.filemanage"})
@EnableScheduling
//@EnableMongoRepositories(basePackages = {"com.neco.sglog.dao"})
public class FileManageCentreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileManageCentreApplication.class,args);
    }
}
