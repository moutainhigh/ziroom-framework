package com.ziroom.framework.ferrari.rabbit.producer;

import com.alibaba.fastjson.JSON;
import com.ziroom.framework.ferrari.common.MessageBodyType;
import com.ziroom.framework.ferrari.rabbit.producer.vo.FerrariMessageVo;
import com.ziroom.framework.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.framework.ferrari.repository.producer.entity.FerrariMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 16:06
 * @Version 1.0
 */
public class FerrariRabbitTemplate extends RabbitTemplate implements InitializingBean, BeanNameAware, ApplicationContextAware {

    Logger logger = LoggerFactory.getLogger(FerrariRabbitTemplate.class);

    private FerrariMessageDao ferrariMessageDao;

    private String beanId;

    private ApplicationContext springApplicationContext;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException {
        this.convertAndSend(exchange, routingKey, object, null, null);
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public void convertAndSend(String exchange, String routingKey, Object object, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {
        FerrariMessageVo ferrariMessage = buildFerrariMessage(exchange, routingKey, object, correlationData);
        FerrariMessage entity = ferrariMessage.findEntity();
        ferrariMessageDao.insert(entity);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    sendAndUpdate(exchange, routingKey, ferrariMessage, messagePostProcessor, correlationData);
                }
            });
        } else {
            sendAndUpdate(exchange, routingKey, ferrariMessage, messagePostProcessor, correlationData);
        }
    }

    @Override
    public void convertAndSend(Object object) throws AmqpException {
        this.convertAndSend(getExchange(), getRoutingKey(), object);
    }

    @Override
    public void convertAndSend(String routingKey, Object object) throws AmqpException {
        this.convertAndSend(getExchange(), routingKey, object);
    }

    @Override
    public void correlationConvertAndSend(Object object, CorrelationData correlationData) throws AmqpException {
        this.convertAndSend(getExchange(), getRoutingKey(), object, null, correlationData);
    }

    @Override
    public void convertAndSend(String routingKey, Object object, CorrelationData correlationData) throws AmqpException {
        this.convertAndSend(getExchange(), routingKey, object, null, correlationData);
    }

    @Override
    public void convertAndSend(String exchange, String routingKey, Object object, CorrelationData correlationData) throws AmqpException {
        this.convertAndSend(exchange, routingKey, object, null, correlationData);
    }

    @Override
    public void convertAndSend(Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
        this.convertAndSend(getExchange(), getRoutingKey(), message, messagePostProcessor, null);
    }

    @Override
    public void convertAndSend(String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
        this.convertAndSend(getExchange(), routingKey, message, messagePostProcessor, null);
    }

    @Override
    public void convertAndSend(Object message, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {
        this.convertAndSend(getExchange(), getRoutingKey(), message, messagePostProcessor, correlationData);
    }

    @Override
    public void convertAndSend(String routingKey, Object message, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) throws AmqpException {
        this.convertAndSend(getExchange(), routingKey, message, messagePostProcessor, correlationData);

    }

    @Override
    public void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
        this.convertAndSend(exchange, routingKey, message, messagePostProcessor, null);
    }

    public void sendAndUpdate(FerrariMessage ferrariMessage) throws ClassNotFoundException {
        CorrelationData correlationData = JSON.parseObject(ferrariMessage.getMsgAttribute(), CorrelationData.class);
        FerrariMessageVo ferrariMessageVo = new FerrariMessageVo(ferrariMessage);
        sendAndUpdate(ferrariMessage.getExchangeKey(), ferrariMessage.getRoutingKey(), ferrariMessageVo, null, correlationData);
    }

    private void sendAndUpdate(String exchange, String routingKey, FerrariMessageVo ferrariMessage, MessagePostProcessor messagePostProcessor, CorrelationData correlationData) {
        FerrariMessageStatus status = FerrariMessageStatus.SUCCESS;
        try {
            if (messagePostProcessor == null) {
                super.convertAndSend(exchange, routingKey, ferrariMessage.getOriginalMessage(), correlationData);
            } else {
                super.convertAndSend(exchange, routingKey, ferrariMessage.getOriginalMessage(), messagePostProcessor, correlationData);
            }
        } catch (Exception e) {
            status = messagePostProcessor != null ? FerrariMessageStatus.FAILED_NOT_RETRY : FerrariMessageStatus.FAILED;
            if (messagePostProcessor != null) {
                throw e;
            }
        } finally {
            updateFerrariMessage(ferrariMessage.findEntity(), status);
        }
    }

    private void updateFerrariMessage(FerrariMessage ferrariMessage, FerrariMessageStatus status) {
        FerrariMessage updateMessage = new FerrariMessage();
        updateMessage.setId(ferrariMessage.getId());
        updateMessage.setMsgStatus(status.getCode());
        updateMessage.setSendTime(status == FerrariMessageStatus.SUCCESS ? new Date() : null);
        try {
            ferrariMessageDao.updateSendStatus(updateMessage);
        } catch (Exception e) {
            logger.error("更新Ferrari消息失败", e);
        }
    }


    public FerrariMessageVo buildFerrariMessage(String exchange, String routingKey, Object object, CorrelationData correlationData) {
        FerrariMessage ferrariMessage = new FerrariMessage();
        ferrariMessage.setBeanId(beanId);
        ferrariMessage.setExchangeKey(exchange);
        ferrariMessage.setRoutingKey(routingKey);
        ferrariMessage.setMsgType("rabbit");
        ferrariMessage.setMsgStatus(FerrariMessageStatus.INIT.getCode());
        if (object instanceof String) {
            ferrariMessage.setMsgBodyType(MessageBodyType.String.getCode());
            ferrariMessage.setMsgBody((String) object);
        } else {
            ferrariMessage.setMsgBodyType(MessageBodyType.Object.getCode());
            ferrariMessage.setMsgBody(JSON.toJSONString(object));
        }
        if (correlationData != null) {
            ferrariMessage.setMsgAttribute(JSON.toJSONString(correlationData));
        }
        ferrariMessage.setMsgBodyClass(object.getClass().getName());
        ferrariMessage.setCreateTime(new Date());
        return new FerrariMessageVo(ferrariMessage, object);
    }


    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.ferrariMessageDao = springApplicationContext.getBean(FerrariMessageDao.class);
    }

    @Override
    public void setBeanName(String beanName) {
        beanId = beanName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springApplicationContext = applicationContext;
    }
}
