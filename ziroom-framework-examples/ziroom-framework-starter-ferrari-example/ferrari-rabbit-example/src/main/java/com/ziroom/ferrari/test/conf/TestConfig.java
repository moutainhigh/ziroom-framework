package com.ziroom.ferrari.test.conf;

import com.ziroom.ferrari.rabbit.producer.FerrariRabbitTemplate;
import com.ziroom.ferrari.repository.core.constant.DialectEnum;
import com.ziroom.ferrari.repository.core.jdbc.JdbcSettings;
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

}
