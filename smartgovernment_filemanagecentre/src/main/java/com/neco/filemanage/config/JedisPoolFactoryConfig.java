package com.neco.filemanage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis客户端链接工厂配置
 *
 * @author ziyuan_deng
 * @create 2020-09-07 20:38
 */
@Configuration
public class JedisPoolFactoryConfig {

    private final RedisProperties redisProperties;

    @Autowired
    public JedisPoolFactoryConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
        config.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
        config.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
        return new JedisPool(config, redisProperties.getHost(), redisProperties.getPort(),
                redisProperties.getTimeout().getNano(), redisProperties.getPassword());
    }
}
