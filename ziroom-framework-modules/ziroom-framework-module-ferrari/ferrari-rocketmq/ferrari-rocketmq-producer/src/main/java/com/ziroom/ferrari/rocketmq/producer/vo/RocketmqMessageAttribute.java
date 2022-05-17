package com.ziroom.ferrari.rocketmq.producer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RocketmqMessageAttribute {

    private String hashKey;

    private Map<String, Object> headers;

}
