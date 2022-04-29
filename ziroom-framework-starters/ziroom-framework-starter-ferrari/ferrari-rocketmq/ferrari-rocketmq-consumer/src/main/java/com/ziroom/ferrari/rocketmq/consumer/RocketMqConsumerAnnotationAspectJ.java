package com.ziroom.ferrari.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.ziroom.ferrari.repository.consumer.FerrariMessageConsumerDao;
import com.ziroom.ferrari.repository.consumer.entity.FerrariMessageConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Objects;

/**
 * @Author: zhangzonggi
 * @Date: 2022-04-29 10:28
 * @Version 1.0
 */
@Aspect
@Component
public class RocketMqConsumerAnnotationAspectJ {
    private final static String ListenerMethodName = "onMessage";
    @Resource
    private FerrariMessageConsumerDao ferrariMessageConsumerDao;

    @Pointcut("@within(org.apache.rocketmq.spring.annotation.RocketMQMessageListener)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void beforeConsumer(JoinPoint joinPoint) {
        // 获取切入点方法名
        String methodName = joinPoint.getSignature().getName();
        if (!methodName.equals(ListenerMethodName)) {
            return;
        }
        RocketMQMessageListener rocketMQMessageListener = findListener(joinPoint);
        if (Objects.isNull(rocketMQMessageListener)) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
                processMessage(rocketMQMessageListener, arg);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private RocketMQMessageListener findListener(JoinPoint joinPoint) {
        RocketMQMessageListener rocketMQMessageListener = null;
        // 获取切入点方法名
        Annotation[] annotations = joinPoint.getSignature().getDeclaringType().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(RocketMQMessageListener.class)) {
                rocketMQMessageListener = (RocketMQMessageListener) annotation;
                break;
            }
        }

        return rocketMQMessageListener;
    }

    private void processMessage(RocketMQMessageListener rocketMQMessageListener, Object messageBody) {
        String topic = null, tags = null, group = null;
        topic = rocketMQMessageListener.topic();
        if (StringUtils.isNotBlank(topic)) {
            String[] tt = topic.split(":");
            topic = tt[0];
            tags = tt.length > 1 ? tt[1] : null;
        }
        group = rocketMQMessageListener.consumerGroup();

        FerrariMessageConsumer entity = buildEntity(topic, tags, group, messageBody);
        ferrariMessageConsumerDao.insert(entity);
    }


    private FerrariMessageConsumer buildEntity(String topic, String tags, String group, Object messageBody) {
        FerrariMessageConsumer consumer = new FerrariMessageConsumer();
        if (messageBody instanceof String) {
            consumer.setMsgBody(String.valueOf(messageBody));
        } else {
            consumer.setMsgBody(JSON.toJSONString(messageBody));
        }
        consumer.setExchangeKey(topic);
        consumer.setMsgId("");
        consumer.setRoutingKey(tags);
        consumer.setQueue(group);
        consumer.setMsgType("rocketmq");
        consumer.setCreateTime(new Date());
        return consumer;
    }
}
