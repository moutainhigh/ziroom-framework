package com.ziroom.framework.autoconfigure.rocketmq;

import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MQAdmin.class})
@AutoConfigureBefore(RocketMQAutoConfiguration.class)
@Import(RocketMQBeanRegistrar.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ZiroomRocketMQAutoConfiguration {


}
