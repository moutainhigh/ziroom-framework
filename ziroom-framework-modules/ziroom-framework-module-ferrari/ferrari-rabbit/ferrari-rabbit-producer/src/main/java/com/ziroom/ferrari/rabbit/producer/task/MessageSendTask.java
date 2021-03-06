package com.ziroom.ferrari.rabbit.producer.task;

import com.ziroom.ferrari.common.lock.FerrariLock;
import com.ziroom.ferrari.rabbit.producer.FerrariRabbitTemplate;
import com.ziroom.ferrari.repository.common.FerrariMessageStatus;
import com.ziroom.ferrari.repository.common.utils.SpringUtils;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 16:24
 * @Version 1.0
 */
@Component("rabbitMessageSendTask")
public class MessageSendTask extends AbstractFerrariTask implements InitializingBean {

    @Resource
    private FerrariMessageDao ferrariMessageDao;

    @Resource
    private FerrariLock ferrariLock;

    private static final String LOCK_KEY = "RabbitMessageSendTask";


    @Override
    public void run() {
        List<FerrariMessage> ferrariMessageList = ferrariMessageDao.findByStatus(FerrariMessageStatus.FAILED.getCode(), "rabbit");
        if (CollectionUtils.isEmpty(ferrariMessageList)) {
            return;
        }
        ferrariMessageList.forEach(this::send);
    }

    @Override
    protected void releaseLock() {
        ferrariLock.releaseLock(LOCK_KEY);
    }

    @Override
    protected boolean lock() {
        return ferrariLock.lock(LOCK_KEY, 5, TimeUnit.MINUTES);
    }


    private void send(FerrariMessage ferrariMessage) {
        try {
            Object bean = SpringUtils.getBean(ferrariMessage.getBeanId());
            if (bean == null) {
                return;
            }
            FerrariRabbitTemplate ferrariRabbitTemplate = (FerrariRabbitTemplate) bean;
            ferrariRabbitTemplate.sendAndUpdate(ferrariMessage);
        } catch (Exception ignored) {

        }
    }


    @Override
    public void afterPropertiesSet() {
        executorService.scheduleWithFixedDelay(this::doTask, 1, 1, TimeUnit.MINUTES);
    }

}
