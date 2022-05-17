package com.ziroom.framework.modules.dubhe.rocketmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubheRocketmqConfiguration {

    @Bean
    public ListenerContainerBeanPostProcessor listenerContainerBeanPostProcessor() {
        return new ListenerContainerBeanPostProcessor();
    }

    @Bean
    public MessageQueueSelectorCustomizer messageQueueSelectorCustomizer() {
        return new MessageQueueSelectorCustomizer();
    }

}
