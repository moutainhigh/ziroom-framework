package com.ziroom.framework.autoconfigure.web;


import cn.hutool.json.JSONUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by liangrk on 2022/5/5.
 */
@Slf4j
public class WebLogMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        // 打印请求相关参数
        log.info("URL : {} | HTTP Method : {} | Class Method : {}.{} | Request Args : {} | IP : {} ",
                request.getRequestURL().toString(),
                request.getMethod(),
                invocation.getMethod().getDeclaringClass().getName(),
                invocation.getMethod().getName(),
                JSONUtil.toJsonStr(invocation.getArguments()),
                request.getRemoteAddr());
        Object result = invocation.proceed();
        // 执行耗时
        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        // 接口结束后换行，方便分割查看
        return result;
    }
    
}
