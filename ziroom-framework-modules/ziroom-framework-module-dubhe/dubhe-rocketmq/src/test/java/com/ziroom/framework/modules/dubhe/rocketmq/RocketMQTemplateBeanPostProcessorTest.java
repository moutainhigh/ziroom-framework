package com.ziroom.framework.modules.dubhe.rocketmq;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = RocketMQTemplateBeanPostProcessorTest.Config.class)
@TestPropertySource(locations = "/application-test.properties")
public class RocketMQTemplateBeanPostProcessorTest {

    @Configuration
    @Import({RocketMQAutoConfiguration.class})
    public static class Config {

        @Bean
        public MessageQueueSelectorCustomizer messageQueueSelectorCustomizer() {
            return new MessageQueueSelectorCustomizer();
        }

    }

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void testMessageQueueSelectorInjected() {
        Collection<RocketMQTemplate> templates = applicationContext.getBeansOfType(RocketMQTemplate.class).values();
        Assert.assertFalse(templates.isEmpty());

        for (RocketMQTemplate template : templates) {
            Assert.assertEquals(DubheMessageQueueSelector.class, template.getMessageQueueSelector().getClass());
        }
    }

}