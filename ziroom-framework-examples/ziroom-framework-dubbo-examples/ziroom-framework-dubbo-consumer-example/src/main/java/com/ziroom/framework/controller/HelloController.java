package com.ziroom.framework.controller;

import com.ziroom.framework.service.HelloService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liangrk on 2022/3/12.
 */
@RestController
@RequestMapping("hello")
@AllArgsConstructor
public class HelloController {

    HelloService helloService;

    @RequestMapping
    public Object test() {
        return helloService.hello();
    }

}
