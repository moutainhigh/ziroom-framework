package com.ziroom.ferrari.test.conf;

import com.ziroom.ferrari.test.listner.RocketMqExampleListner;
import com.ziroom.ferrari.test.service.TestRocketmqService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:19
 * @Version 1.0
 */
@Configuration
public class TestRocketmqConsumerConfig {

    private String nameServer = "10.216.9.189:9876";

    @Resource
    private RocketMqExampleListner exampleListner;

    @PostConstruct
    public DefaultMQPushConsumer createDefaultMQPushConsumer() throws MQClientException {
        return createDefaultMQPushConsumer(TestRocketmqService.NOTICE_TOPIC, TestRocketmqService.CONSUMER_GROUP_BEAN, "*", exampleListner);
    }

    public DefaultMQPushConsumer createDefaultMQPushConsumer(String topic, String groupName, String subExpression, MessageListenerConcurrently messageListener) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(nameServer);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.subscribe(topic, subExpression);
        consumer.setVipChannelEnabled(false);
        consumer.registerMessageListener(messageListener);
        consumer.start();
        return consumer;
    }
}
