package com.ziroom.ferrari.test.conf;

import com.ziroom.ferrari.rabbit.producer.FerrariRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:19
 * @Version 1.0
 */
@Configuration
public class TestRabbitConfig {

    private String mqClusterAddress = "10.216.4.233:5672,10.216.4.234:5672,10.216.4.236:5672";
    private String mqUsername = "crm_trade-service-hiesign";
    private String mqPassword = "PAenGfl7jf";
    private String virtualHost = "/crm";


    @Bean(value = "connectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(mqClusterAddress);
        connectionFactory.setUsername(mqUsername);
        connectionFactory.setPassword(mqPassword);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean
    public FerrariRabbitTemplate ferrariRabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory, RetryTemplate retryTemplate) {
        FerrariRabbitTemplate template = new FerrariRabbitTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setRetryTemplate(retryTemplate);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

}
