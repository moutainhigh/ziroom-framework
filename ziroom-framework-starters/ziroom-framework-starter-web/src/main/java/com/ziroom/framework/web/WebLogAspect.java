//package com.ziroom.qms.web.aspect;
//
//import cn.hutool.json.JSONUtil;
//import com.ziroom.qms.common.utils.HttpRequestUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author zhangkx1.ziroom.com
// * @create 2022/2/21 17:31
// */
//@Slf4j
//@Aspect
//@Order(0)//配置Spring注解事务时,在事务之前切换数据源
//@Component
//public class WebLogAspect {
//    /**
//     * 换行符
//     */
//    private static final String LINE_SEPARATOR = System.lineSeparator();
//
//    /**
//     * 环绕
//     *
//     * @param proceedingJoinPoint proceedingJoinPoint
//     * @return Object
//     * @throws Throwable throwable
//     */
//    @Around("execution(* com.ziroom.qms.web.controller..*.*(..))")
//    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        long startTime = System.currentTimeMillis();
//        // 开始打印请求日志
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        assert attributes != null;
//        HttpServletRequest request = attributes.getRequest();
//        // 打印请求相关参数
//        log.info("========================================== Start ==========================================");
//        log.info("UserName : {} | UserCode : {} | URL : {} | HTTP Method : {} | Class Method : {}.{} | IP : {} | Request Args : {}",
//                HttpRequestUtil.getCurrentUserName(),
//                HttpRequestUtil.getCurrentUserCode(),
//                request.getRequestURL().toString(),
//                request.getMethod(),
//                proceedingJoinPoint.getSignature().getDeclaringTypeName(),
//                proceedingJoinPoint.getSignature().getName(),
//                request.getRemoteAddr(), JSONUtil.toJsonStr(proceedingJoinPoint.getArgs()));
//        Object result = proceedingJoinPoint.proceed();
//        // 执行耗时
//        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
//        // 接口结束后换行，方便分割查看
//        log.info("=========================================== End ===========================================" + LINE_SEPARATOR);
//        return result;
//    }
//}
