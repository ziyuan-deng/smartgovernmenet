package com.neco.sglog.aop;


import com.alibaba.fastjson.JSON;
import com.neco.sglog.annotation.SgLog;
import com.neco.sglog.event.RequestLogEvent;
import com.neco.sglog.model.RequestLogInfo;
import com.neco.sglog.utils.DateUtils;
import com.neco.sglog.utils.IpUtils;
import com.neco.sglog.utils.SpringContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 各微服务请求日志收集切面
 * @author ziyuan_deng
 * @date 2020/9/10
 */
@Aspect
@Component
public class LogAspect {

    @Value("${spring.application.name}")
    private String serverId;

    @Pointcut("@annotation(com.neco.sglog.annotation.SgLog)")
    public void  webLog(){
    }

    /**
     * 环绕切面微服务请求日志
      * @param point
     * @return
     * @throws Throwable
     */
    @Around(value = "webLog()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Logger log = LoggerFactory.getLogger(LogAspect.class);
        long begin = System.currentTimeMillis();
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        SgLog logMethod = method.getAnnotation(SgLog.class);

        RequestLogInfo requestLogInfo = new RequestLogInfo();
        if (serverId != null) {
            requestLogInfo.setMicroservice(serverId);
        }
        String desc = logMethod.description();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取ip地址
        String ipAddress = IpUtils.getIpAddr(request);
        requestLogInfo.setFromIp(ipAddress);
        //访问日期
        requestLogInfo.setVisitTime(DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        //请求参数
        Object[] params = point.getArgs();
        String paramStr =  getRequestParams(params);
        requestLogInfo.setSubmitParam(paramStr);
        log.info("======请求开始=======");
        log.info("======请求开始："+ request.getRequestURI());
        log.info("请求接口描述："+desc);
        log.info("请求方法：{},{}",signature.getDeclaringTypeName(),((MethodSignature) signature).getMethod());
        log.info("请求参数：{}", paramStr);
        Object result = point.proceed();

        long end = System.currentTimeMillis();

        log.info("请求耗时：{} ms",end - begin);
        double last = (end - begin) * 0.001;
        requestLogInfo.setDescription(desc+"成功");
        requestLogInfo.setTimeConsume(last + "秒");
        log.info("请求返回：{} ",JSON.toJSONString(result));
        requestLogInfo.setReturnParam(JSON.toJSONString(result));
        requestLogInfo.setResult("成功");
        //发送保存日志信息的消息
        SpringContextHolder.publishEvent(new RequestLogEvent(requestLogInfo));
        log.info("================   请求结束=========");
        return result;
    }

    /**
     * 系列化请求参数
     * @param params
     * @return
     */
    private String getRequestParams(Object[] params) {
        StringBuffer requestBuild = new StringBuffer();
        for (Object param : params) {
            if (param instanceof HttpServletRequest || param instanceof HttpServletResponse || param instanceof MultipartFile){
                continue;
            }
            requestBuild.append(param == null ? "" : JSON.toJSONString(param));
        }
        String paramStr = requestBuild.toString();
        return paramStr;
    }

    @AfterThrowing(pointcut = "webLog()", throwing = "e")
    public void afterThrowing(JoinPoint point, Exception e) {
        // 处理日志信息
        Logger log = LoggerFactory.getLogger(LogAspect.class);
        long begin = System.currentTimeMillis();
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        SgLog logMethod = method.getAnnotation(SgLog.class);

        RequestLogInfo requestLogInfo = new RequestLogInfo();
        if (serverId != null) {
            requestLogInfo.setMicroservice(serverId);
        }
        String n = logMethod.serverName();
        String desc = logMethod.description();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取ip地址
        String ipAddress = IpUtils.getIpAddr(request);
        requestLogInfo.setFromIp(ipAddress);
        requestLogInfo.setVisitTime(DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        Object[] params = point.getArgs();
        String requestParams = getRequestParams(params);
        requestLogInfo.setSubmitParam(requestParams);
        String requestURI = request.getRequestURI();
        log.info("======请求开始=======");
        log.info("======请求url:"+requestURI);
        log.info("请求接口描述："+desc);
        log.info("请求方法：{},{}",signature.getDeclaringTypeName(),((MethodSignature) signature).getMethod());
        log.info("请求参数：{}",requestParams);
        //Object result = point.proceed();

        long end = System.currentTimeMillis();

        log.info("请求耗时：{} ms",end - begin);
        double last = (end - begin) * 0.001;
        requestLogInfo.setDescription(desc+"失败");
        requestLogInfo.setTimeConsume(last + "秒");
        if (e != null) {
            requestLogInfo.setReturnParam(e.getMessage());
        }
        requestLogInfo.setResult("失败");
        //发送保存日志信息的消息
        SpringContextHolder.publishEvent(new RequestLogEvent(requestLogInfo));
        log.info("================   请求结束=========");

    }
}
