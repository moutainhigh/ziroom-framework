package com.ziroom.framework.modules.dubhe.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
public class MessageQueueSelectorCustomizer implements ApplicationListener<ContextRefreshedEvent> {

    private final DubheMessageQueueSelector messageQueueSelector = new DubheMessageQueueSelector();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBeansOfType(RocketMQTemplate.class).values().forEach(rocketMQTemplate -> {
            rocketMQTemplate.setMessageQueueSelector(messageQueueSelector);
            log.info("Setup MessageQueueSelector for {}", rocketMQTemplate);
        });
    }
}
