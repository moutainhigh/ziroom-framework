package com.ziroom.ferrari.test.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziroom.ferrari.rocketmq.producer.FerrariRocketmqTemplate;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:19
 * @Version 1.0
 */
@Configuration
public class TestRocketmqProducerConfig {

    private String nameServer = "10.216.9.189:9876";

    @Bean
    public DefaultMQProducer rocketMqProducer() {
        String producerGroup = "rmq-producer";
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);

        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(3000);
        producer.setRetryTimesWhenSendFailed(3);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setMaxMessageSize(1024 * 1024 * 4);
        producer.setCompressMsgBodyOverHowmuch(2);
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        return producer;
    }

    @Bean
    public FerrariRocketmqTemplate ferrariRocketmqTemplate(@Qualifier("rocketMqProducer") DefaultMQProducer defaultMQProducer, ObjectMapper rocketMQMessageObjectMapper) {
        FerrariRocketmqTemplate template = new FerrariRocketmqTemplate();
        template.setProducer(defaultMQProducer);
        template.setObjectMapper(rocketMQMessageObjectMapper);
//        template.setMessageConverter(new MappingJackson2MessageConverter());
        return template;
    }
}
