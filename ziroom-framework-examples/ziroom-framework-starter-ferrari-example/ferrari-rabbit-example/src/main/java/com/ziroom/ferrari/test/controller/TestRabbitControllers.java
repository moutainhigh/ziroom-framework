package com.ziroom.ferrari.test.controller;

import com.ziroom.ferrari.test.service.TestRabbitService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:14
 * @Version 1.0
 */
@RestController
@RequestMapping("/rabbit")
public class TestRabbitControllers {

    @Resource
    private TestRabbitService testRabbitService;

    @RequestMapping("test")
    public String test() {
        testRabbitService.test();
        return "success";
    }

    @RequestMapping("test2")
    public String test2() {
        testRabbitService.test2();
        return "success";
    }

    @RequestMapping("test3")
    public String test3() {
        testRabbitService.testWithException();
        return "success";
    }

    @RequestMapping("test4")
    public String test4() {
        testRabbitService.testWithException2();
        return "success";
    }
}
