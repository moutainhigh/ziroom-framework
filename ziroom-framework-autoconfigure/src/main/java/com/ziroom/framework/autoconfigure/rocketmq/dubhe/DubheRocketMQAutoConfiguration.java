package com.ziroom.framework.autoconfigure.rocketmq.dubhe;

import com.ziroom.framework.modules.dubhe.rocketmq.DubheRocketmqConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "dubhe.rocketmq.enabled", havingValue = "true")
@Import(DubheRocketmqConfiguration.class)
public class DubheRocketMQAutoConfiguration {

}
