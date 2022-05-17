package com.ziroom.framework.example.controller;

import com.ziroom.framework.common.api.pojo.ResponseData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apollo")
public class ApolloConfigController {

    @Value("${hello-message}")
    private String helloMessage;

    @GetMapping("/hello")
    public ResponseData<String> getConfig() {
        return ResponseData.success(this.helloMessage);
    }

}
