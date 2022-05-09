package com.ziroom.ferrari.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ziroom.ferrari.common.MessageBodyType;
import com.ziroom.ferrari.repository.common.FerrariMessageStatus;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import com.ziroom.ferrari.rocketmq.producer.vo.FerrariMessageVo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @Author: zhangzongqi
 * @Date: 2021/8/27 16:06
 * @Version 1.0
 */
public class FerrariRocketmqTemplate extends RocketMQTemplate implements InitializingBean, BeanNameAware, ApplicationContextAware {

    Logger logger = LoggerFactory.getLogger(FerrariRocketmqTemplate.class);

    private FerrariMessageDao ferrariMessageDao;

    private String beanId;

    private ApplicationContext springApplicationContext;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public <T> void syncSend(String topic, String tags, T message) throws JsonProcessingException {
        FerrariMessageVo ferrariMessage = buildFerrariMessage(topic, tags, message);
        FerrariMessage entity = ferrariMessage.findEntity();
        ferrariMessageDao.insert(entity);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    syncSendAndUpdate(topic, tags, ferrariMessage);
                }
            });
        } else {
            syncSendAndUpdate(topic, tags, ferrariMessage);
        }
    }

    private <T> void syncSendAndUpdate(String topic, String tags, FerrariMessageVo ferrariMessage) {
        FerrariMessageStatus status = FerrariMessageStatus.SUCCESS;
        try {
            String destination = null;
            if (StringUtils.isEmpty(tags)) {
                destination = topic;
            } else {
                destination = topic + ":" + tags;
            }

            super.syncSend(destination, ferrariMessage.getOriginalMessage());
        } catch (Exception e) {
            status = FerrariMessageStatus.FAILED;
            throw e;
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


    public FerrariMessageVo buildFerrariMessage(String topic, String tags, Object object) throws JsonProcessingException {
        FerrariMessage ferrariMessage = new FerrariMessage();
        ferrariMessage.setBeanId(beanId);
        ferrariMessage.setExchangeKey(topic);
        ferrariMessage.setRoutingKey(tags);
        ferrariMessage.setMsgType("rocketmq");
        ferrariMessage.setMsgStatus(FerrariMessageStatus.INIT.getCode());
        if (object instanceof String) {
            ferrariMessage.setMsgBodyType(MessageBodyType.String.getCode());
            ferrariMessage.setMsgBody((String) object);
        } else {
            ferrariMessage.setMsgBodyType(MessageBodyType.Object.getCode());
            ferrariMessage.setMsgBody(JSON.toJSONString(object));
        }

        ferrariMessage.setMsgBodyClass(object.getClass().getName());
        ferrariMessage.setCreateTime(new Date());
        return new FerrariMessageVo(ferrariMessage, object);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
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