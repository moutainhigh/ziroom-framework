package com.ziroom.framework.autoconfigure.rocketmq;

import lombok.Data;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
public class ZiroomRocketMQProperties extends RocketMQProperties {

    private String producerBeanName;
    private String templateBeanName;

    private Ferrari ferrari = new Ferrari();

    @Data
    public static class Ferrari {
        boolean enabled = false;
    }

}
