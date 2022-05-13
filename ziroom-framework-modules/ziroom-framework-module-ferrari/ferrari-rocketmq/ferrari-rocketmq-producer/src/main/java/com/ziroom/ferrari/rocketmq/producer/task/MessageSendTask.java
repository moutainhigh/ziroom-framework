package com.ziroom.ferrari.rocketmq.producer.task;

import com.ziroom.ferrari.common.lock.FerrariLock;
import com.ziroom.ferrari.repository.common.utils.SpringUtils;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import com.ziroom.ferrari.repository.common.FerrariMessageStatus;
import com.ziroom.ferrari.rocketmq.producer.FerrariRocketmqTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangzonggi
 * @Date: 2022-04-29 10:28
 * @Version 1.0
 */
@Component("rocketMQMessageSendTask")
public class MessageSendTask extends AbstractFerrariTask implements InitializingBean {

    @Resource
    private FerrariMessageDao ferrariMessageDao;

    @Resource
    private FerrariLock ferrariLock;

    private static final String LOCK_KEY = "RocketMQMessageSendTask";


    @Override
    public void run() {
        List<FerrariMessage> ferrariMessageList = ferrariMessageDao.findByStatus(FerrariMessageStatus.FAILED.getCode(), "rocketmq");
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
            FerrariRocketmqTemplate ferrariRocketmqTemplate = (FerrariRocketmqTemplate) bean;
            ferrariRocketmqTemplate.sendAndUpdate(ferrariMessage);
        } catch (Exception ignored) {

        }
    }


    @Override
    public void afterPropertiesSet() {
        executorService.scheduleWithFixedDelay(this::doTask, 1, 1, TimeUnit.MINUTES);
    }

}
