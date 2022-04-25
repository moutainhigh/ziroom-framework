package com.ziroom.framework.ferrari.test;

import com.ziroom.ferrari.rabbit.producer.conf.FerrariConf;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:06
 * @Version 1.0
 */

@Import(FerrariConf.class)
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.ziroom.ferrari.test.dao")
public class FerrariTestServer {

    public static void main(String[] args) {
        SpringApplication.run(FerrariTestServer.class, args);
    }
}
