package com.ziroom.framework.example.controller;

import cn.hutool.core.map.MapUtil;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.ziroom.framework.common.api.pojo.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/apollo")
@Api(tags = "Apollo模块")
public class ApolloConfigController {

    @Value("${hello-message}")
    private String helloMessage;

    @ApiOperation("读取 apollo 配置")
    @GetMapping("/hello")
    public Map<String, Object> getConfig() {
        return MapUtil.of("config", this.helloMessage);
    }

}
