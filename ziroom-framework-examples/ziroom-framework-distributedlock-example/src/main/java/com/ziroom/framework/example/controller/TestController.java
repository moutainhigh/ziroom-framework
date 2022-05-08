package com.ziroom.framework.example.controller;

import com.ziroom.framework.module.distributedlock.annotation.DistributedLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liangrk on 2022/5/4.
 */
@RestController
public class TestController {

    @GetMapping("test")
    @DistributedLock(lockName = "ssss")
    public String test(String ssss) throws InterruptedException {
        Thread.sleep(5000);
        return "1";
    }

}
