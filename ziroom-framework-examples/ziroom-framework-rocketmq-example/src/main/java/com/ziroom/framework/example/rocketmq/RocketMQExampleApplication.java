package com.ziroom.framework.example.rocketmq;

import com.ziroom.ferrari.rocketmq.consumer.EnableFerrariRocketConsumer;
import com.ziroom.ferrari.rocketmq.producer.EnableFerrariRocketProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFerrariRocketProducer
@EnableFerrariRocketConsumer
public class RocketMQExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocketMQExampleApplication.class);
    }
}
