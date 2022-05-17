package com.ziroom.framework.modules.dubhe.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = ListenerContainerBeanPostProcessorTest.Config.class)
@TestPropertySource(locations = "/application-test2.properties")
public class ListenerContainerBeanPostProcessorTest {

    @Configuration
    @Import(RocketMQAutoConfiguration.class)
    public static class Config {

        @Bean
        public RocketMqExampleListener listener() {
            return new RocketMqExampleListener();
        }

        @Bean
        public ListenerContainerBeanPostProcessor postProcessor() {
            return new ListenerContainerBeanPostProcessor();
        }

    }

    @Slf4j
    @RocketMQMessageListener(
        topic = "mycenter-common-notice-topic-z",
        consumerGroup = "test-group"
    )
    public static class RocketMqExampleListener implements RocketMQListener<String> {

        public void onMessage(String noticeReq) {
            try {
                handelMessage(noticeReq);
            } catch (Exception e) {
                log.error("消息变更Mq处理系统异常", e);
            }
        }

        private void handelMessage(String noticeReq) {
//        String data = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            log.info("消息变更Mq通知: [{}]", noticeReq);
        }

    }

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void testAllocateMessageQueueStrategyInjected() {
        Collection<DefaultRocketMQListenerContainer> containers = applicationContext.getBeansOfType(DefaultRocketMQListenerContainer.class).values();
        Assert.assertFalse(containers.isEmpty());

        for (DefaultRocketMQListenerContainer container : containers) {
            AllocateMessageQueueStrategy strategy = container.getConsumer().getAllocateMessageQueueStrategy();
            Assert.assertEquals(DubheAllocateMessageQueueStrategy.class, strategy.getClass());
        }
    }

}