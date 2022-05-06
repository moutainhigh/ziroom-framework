package com.ziroom.framework.example.controller1;

import com.ziroom.framework.module.distributedlock.annotation.DistributedLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liangrk on 2022/5/5.
 */
@RestController
public class TestController1 {

    @GetMapping("test1")
    @DistributedLock(lockName = "ssss")
    public String test(String ssss) throws InterruptedException {
        Thread.sleep(5000);
        return "1";
    }

    @GetMapping("test2")
    public String test2() throws InterruptedException {
        Thread.sleep(5000);
        return "1";
    }

}