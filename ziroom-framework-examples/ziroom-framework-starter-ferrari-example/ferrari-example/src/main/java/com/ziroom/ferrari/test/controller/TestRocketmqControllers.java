package com.ziroom.ferrari.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ziroom.ferrari.test.service.TestRocketmqService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:14
 * @Version 1.0
 */
@RestController
@RequestMapping("/rocketmq")
public class TestRocketmqControllers {

    @Resource
    private TestRocketmqService testRocketmqService;

    @RequestMapping("test")
    public String test() throws JsonProcessingException {
        testRocketmqService.test();
        return "success";
    }

    @RequestMapping("batch")
    public String batchTest() throws JsonProcessingException {
        testRocketmqService.batchTest();
        return "success";
    }
//
//    @RequestMapping("test2")
//    public String test2() {
//        testRocketmqService.test2();
//        return "success";
//    }
//
//    @RequestMapping("test3")
//    public String test3() {
//        testRocketmqService.testWithException();
//        return "success";
//    }
//
//    @RequestMapping("test4")
//    public String test4() {
//        testRocketmqService.testWithException2();
//        return "success";
//    }
}
