package com.ziroom.framework.autoconfigure.configx;

import com.ziroom.framework.autoconfigure.rocketmq.RocketMQPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class ConfigXEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        environment.getPropertySources().addFirst(new RocketMQPropertySource());
    }

}
