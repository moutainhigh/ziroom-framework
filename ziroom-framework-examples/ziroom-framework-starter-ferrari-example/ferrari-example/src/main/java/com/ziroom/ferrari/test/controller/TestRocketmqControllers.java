package com.ziroom.ferrari.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ziroom.ferrari.test.service.TestRocketmqService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping("send")
    public String test(@RequestParam(defaultValue = "false") boolean async, @RequestParam(defaultValue = "false") boolean rollback) throws JsonProcessingException {
        testRocketmqService.test(async, rollback);
        return "success";
    }

    @RequestMapping("batchSend")
    public String batchTest(@RequestParam(defaultValue = "false") boolean async) throws JsonProcessingException {
        testRocketmqService.batchTest(async);
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
