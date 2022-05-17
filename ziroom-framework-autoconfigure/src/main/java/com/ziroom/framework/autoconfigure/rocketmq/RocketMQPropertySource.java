package com.ziroom.framework.autoconfigure.rocketmq;

import com.ziroom.framework.autoconfigure.configx.ConfigXPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class RocketMQPropertySource extends ConfigXPropertySource implements EnvironmentPostProcessor {
    public RocketMQPropertySource() {
        super("ziroomRocketMQ", "rocketmq-*", "ziroom.rocketmq.");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 如果
    }
}
