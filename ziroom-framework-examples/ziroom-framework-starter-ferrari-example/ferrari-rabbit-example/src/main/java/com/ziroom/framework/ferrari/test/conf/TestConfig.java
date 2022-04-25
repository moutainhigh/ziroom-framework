package com.ziroom.framework.ferrari.test.conf;

import com.ziroom.ferrari.rabbit.producer.FerrariRabbitTemplate;
import com.ziroom.framework.ferrari.repository.core.constant.DialectEnum;
import com.ziroom.framework.ferrari.repository.core.jdbc.JdbcSettings;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:19
 * @Version 1.0
 */
@Configuration
public class TestConfig {

    private String mqClusterAddress = "10.216.4.233:5672,10.216.4.234:5672,10.216.4.236:5672";
    private String mqUsername = "crm_trade-service-hiesign";
    private String mqPassword = "PAenGfl7jf";
    private String virtualHost = "/crm";


    @Resource
    private DataSource dataSource;

    @Bean(name = "masterOnlyJdbcSettings")
    public JdbcSettings masterOnlyJdbcSettings() {
        JdbcSettings jdbcSettings = new JdbcSettings();
        List<DataSource> writeDatasource = new ArrayList<>(1);
        writeDatasource.add(dataSource);
        List<DataSource> readDatasource = new ArrayList<>(1);
        readDatasource.add(dataSource);
        jdbcSettings.setWriteDataSource(writeDatasource);
        jdbcSettings.setReadDataSource(readDatasource);
        jdbcSettings.setDialectEnum(DialectEnum.MYSQL);
        return jdbcSettings;
    }

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
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
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
