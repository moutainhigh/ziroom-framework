/*
 * Copyright ziroom.com.
 */

package com.ziroom.framework.autoconfigure.cache;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自如rabbitmq自动注册
 * @author zhaoy13,liangrk,kanggh
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ RabbitTemplate.class, Channel.class })
@AutoConfigureBefore(RabbitAutoConfiguration.class)
@Import({RabbitPropertySourcesProcessor.class})
public class ZiroomRabbitAutoConfiguration {

}
