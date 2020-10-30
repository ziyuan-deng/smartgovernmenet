package com.neco.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author ziyuan_deng
 * @date 2020/9/16
 */
@SpringBootApplication
@EnableDiscoveryClient  //服务注册与发现
@ComponentScan(basePackages = {"com.neco.sglog"})
@ComponentScan(basePackages = {"com.neco.message"})
//@EnableMongoRepositories(basePackages = {"com.neco.sglog.dao"})
public class MessageCentreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageCentreApplication.class,args);
    }
}
