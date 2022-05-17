package com.ziroom.framework.modules.dubhe.rocketmq;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ListenerContainerPostProcessor 负责将 DubheAllocateMessageQueueStrategy 注入 ListenerContainer 中
 */
@Slf4j
public class ListenerContainerBeanPostProcessor implements BeanPostProcessor {

    private final DubheAllocateMessageQueueStrategy allocateMessageQueueStrategy
        = new DubheAllocateMessageQueueStrategy();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DefaultRocketMQListenerContainer) {
            DefaultRocketMQListenerContainer listenerContainer = (DefaultRocketMQListenerContainer) bean;

            DefaultMQPushConsumer consumer = listenerContainer.getConsumer();
            consumer.setAllocateMessageQueueStrategy(this.allocateMessageQueueStrategy);
            consumer.setInstanceName(getInstanceName(consumer.getInstanceName()));

            log.info("Setup DubheAllocateMessageQueueStrategy for: {}", bean);
        }
        return bean;
    }

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public static String getInstanceName(String originName) {
        String env = System.getenv("APPLICATION_ENV");
        if (env == null) {
            env = "LOCAL";
        }

        String instanceName = String.valueOf(UtilAll.getPid()) + "-" + COUNT.incrementAndGet();
        instanceName = (env.equals("stable") ? "1" : "0") + "-" + instanceName;
        instanceName = (StringUtils.isNotEmpty(env) ? env : "stable") + "-" + instanceName;
        return instanceName + "$$" + originName;
    }

}
