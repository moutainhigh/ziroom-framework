package com.ziroom.framework.module.distributedlock;

import com.ziroom.framework.module.distributedlock.annotation.DistributedLock;
import java.util.concurrent.locks.Lock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author zhangkx1
 */
@Slf4j
@Aspect
public class DistributedLockAspect {

    LockGetter lockGetter;

    @Value("${spring.application.name:}")
    public String applicationName;

    public DistributedLockAspect(LockGetter lockGetter) {
        this.lockGetter = lockGetter;
    }

    @Pointcut("@annotation(com.ziroom.framework.module.distributedlock.annotation.DistributedLock)")
    public void pointCut() {

    }

//    PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":",  true);
    ExpressionParser parser = new SpelExpressionParser();

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DistributedLock distributedLockAnnotation = AnnotationUtils.getAnnotation(methodSignature.getMethod(), DistributedLock.class);

        assert distributedLockAnnotation != null;
        String lockNameExpression = distributedLockAnnotation.lockNameExpression();

        Expression expression = parser.parseExpression(lockNameExpression);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            Object value = args[i] == null ? "" : args[i];
            context.setVariable(parameterNames[i], value);
            if (context.lookupVariable("param" + (i + 1)) == null) {
                context.setVariable("param" + (i + 1), value);
            }
        }
        context.setVariable("applicationName", applicationName);
        context.setVariable("className", methodSignature.getDeclaringType().getName());
        context.setVariable("method", methodSignature.getName());

        String lockName = expression.getValue(context, String.class);

        log.info("加分布式锁，lockName = {}", lockName);
        Lock lock = lockGetter.getLock(lockName);
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
