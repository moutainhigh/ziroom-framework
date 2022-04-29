package com.ziroom.ferrari.rabbit.consumer;

import com.ziroom.ferrari.repository.consumer.FerrariMessageConsumerDao;
import com.ziroom.ferrari.repository.consumer.entity.FerrariMessageConsumer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Author: J.T.
 * @Date: 2022-3-10 10:28
 * @Version 1.0
 */
@Aspect
@Component
public class RabbitMqConsumerAnnotationAspectJ {

    @Resource
    private FerrariMessageConsumerDao ferrariMessageConsumerDao;

    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void beforeConsumer(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            try {
                if (arg instanceof Message) {
                    processMessage((Message) arg);
                    break;
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

    }

    private void processMessage(Message message) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        MessageProperties messageProperties = message.getMessageProperties();
        FerrariMessageConsumer entity = buildEntity(messageBody, messageProperties);
        ferrariMessageConsumerDao.insert(entity);
    }

    private FerrariMessageConsumer buildEntity(String messageBody, MessageProperties messageProperties) {
        FerrariMessageConsumer consumer = new FerrariMessageConsumer();
        consumer.setMsgBody(messageBody);
        consumer.setExchangeKey(messageProperties.getReceivedExchange());
        consumer.setMsgId(messageProperties.getMessageId());
        consumer.setRoutingKey(messageProperties.getReceivedRoutingKey());
        consumer.setQueue(messageProperties.getConsumerQueue());
        consumer.setMsgType("rabbit");
        consumer.setCreateTime(new Date());
        return consumer;
    }
}
