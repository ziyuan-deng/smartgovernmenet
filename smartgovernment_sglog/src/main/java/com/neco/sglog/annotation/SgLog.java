package com.neco.sglog.annotation;

import java.lang.annotation.*;

/**
 * 启用组件注解
 * @author ziyuan_deng
 * @date 2020/9/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SgLog {

    /**
     * 服务(子系统)名 默认取${spring.application.name}
     */
    String serverName() default "";

    /**
     * 模块名
     */
    String module() default "";

    /**
     * 接口功能描述信息
     */
    String description() default "";
}
