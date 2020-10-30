package com.neco.sglog.dao;

import com.neco.sglog.model.RequestLogInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 操作mongoDB接口
 * @author ziyuan_deng
 * @date 2020/9/9
 */
public interface RequestLogRepository extends MongoRepository<RequestLogInfo,String> {
}
