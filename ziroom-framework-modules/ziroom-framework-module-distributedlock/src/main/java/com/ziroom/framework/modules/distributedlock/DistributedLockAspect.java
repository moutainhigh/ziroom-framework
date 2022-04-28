package com.ziroom.framework.modules.distributedlock;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangkx1
 */
@Slf4j
@Aspect
@Component
public class DistributedLockAspect {

    @Resource
    private Redisson redisson;

    @Pointcut("@annotation(com.ziroom.framework.modules.distributedlock.DistributedLock)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DistributedLock distributedLockAnnotation = AnnotationUtils.getAnnotation(methodSignature.getMethod(), DistributedLock.class);

        assert distributedLockAnnotation != null;
        String lockNameExpression = distributedLockAnnotation.lockName();
        List<String> expressionList = StrUtil.split(lockNameExpression, ".");
        String firstExpression = expressionList.get(0);
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        int index = ArrayUtil.indexOf(parameterNames, firstExpression);
        if (index == ArrayUtil.INDEX_NOT_FOUND) {
            throw new DistributedLockException(DistributedLockCode.TRY_LOCK_FAIL.getCode(), "DistributedLockAspect：获取锁名称失败");
        }
        Object arg = args[index];
        String lockName = methodSignature.getDeclaringType().getName()+":"+methodSignature.getMethod().getName() + ":";
        if (expressionList.size() > 1) {
            lockName = lockName + BeanUtil.getProperty(arg, expressionList.get(1));
        } else {
            lockName = lockName + arg.toString();
        }
        if (StrUtil.isBlank(lockName)) {
            throw new DistributedLockException(DistributedLockCode.TRY_LOCK_FAIL.getCode(), "DistributedLockAspect：锁名称为空");
        }

        log.info("加分布式锁，lockName = {}", lockName);
        RLock lock = redisson.getLock(lockName);
        if (!lock.tryLock()) {
            throw new DistributedLockException(DistributedLockCode.TRY_LOCK_FAIL.getCode(), "资源正在使用，请稍后再试");
        }
        try {
            return joinPoint.proceed();
        } finally {
            //释放锁
            lock.unlock();
            log.info("释放分布式锁，lockName = {}", lockName);
        }
    }

}
