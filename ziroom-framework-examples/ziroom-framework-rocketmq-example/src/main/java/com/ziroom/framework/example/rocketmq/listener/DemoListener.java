package com.ziroom.framework.example.rocketmq.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "test-topic", consumerGroup = "test-group")
public class DemoListener implements RocketMQListener {

    @Override
    public void onMessage(Object message) {
        System.out.println("------> " + message);
    }
}
