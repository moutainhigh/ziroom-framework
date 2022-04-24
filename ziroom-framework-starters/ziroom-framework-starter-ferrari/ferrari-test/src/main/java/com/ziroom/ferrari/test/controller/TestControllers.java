package com.ziroom.ferrari.test.controller;

import com.ziroom.ferrari.test.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:14
 * @Version 1.0
 */
@RestController
public class TestControllers {

    @Resource
    private TestService testService;

    @RequestMapping("test")
    public String test() {
        testService.test();
        return "success";
    }

    @RequestMapping("test2")
    public String test2() {
        testService.test2();
        return "success";
    }

    @RequestMapping("test3")
    public String test3() {
        testService.testWithException();
        return "success";
    }

    @RequestMapping("test4")
    public String test4() {
        testService.testWithException2();
        return "success";
    }
}
