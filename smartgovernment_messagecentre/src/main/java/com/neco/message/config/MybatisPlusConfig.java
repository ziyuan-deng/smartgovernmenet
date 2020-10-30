package com.neco.message.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * mybatisplus配置类
 *
 * @author ziyuan_deng
 * @create 2020-09-05 17:04
 */
@Configuration
@EnableTransactionManagement
@AutoConfigureAfter({DruidConfig.class})
@ConfigurationProperties(prefix = "mybatis-plus")
@MapperScan({"com.neco.message.mapper"})
public class MybatisPlusConfig {

    private Logger logger = LoggerFactory.getLogger(MybatisPlusConfig.class);

    @Resource(name = "druidDataSource")
    private DataSource druidDataSource;

    @Value("${mybatis-plus.type-aliases-package}")
    private String typeAliasesPackage;
    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;
    @Value("${pageInterceptor.dialectType}")
    private String dialectType;
    @Value("${pageInterceptor.localPage}")
    private boolean localPage;

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory() {
        try {
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
            sessionFactory.setDataSource(druidDataSource);
            sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
            sessionFactory.setMapperLocations(resourcePatternResolver .getResources(mapperLocations));
            sessionFactory.setPlugins(new Interceptor[]{paginationInterceptor() });//添加分页功能
            return sessionFactory.getObject();
        } catch (Exception e) {
            logger.warn("Could not confiure mybatis session factory");
            e.printStackTrace();
            return null;
        }
    }
    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(druidDataSource);
    }
    /*@Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }*/

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setDialectType(dialectType);
        interceptor.setLocalPage(localPage);
        return interceptor;
    }

}
