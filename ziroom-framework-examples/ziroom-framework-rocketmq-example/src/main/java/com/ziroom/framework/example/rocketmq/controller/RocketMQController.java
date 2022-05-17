package com.ziroom.framework.example.rocketmq.controller;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RocketMQController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping
    public void send(String msg) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msg", msg);
        rocketMQTemplate.syncSend("test-topic", payload);
    }

}
