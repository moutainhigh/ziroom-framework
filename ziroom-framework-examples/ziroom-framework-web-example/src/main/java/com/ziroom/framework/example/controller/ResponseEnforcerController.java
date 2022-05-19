package com.ziroom.framework.example.controller;

import cn.hutool.core.map.MapUtil;
import com.ziroom.framework.common.api.pojo.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/response")
@RestController
@Api(value = "强制返回值范例，注意由于 swagger 是直接解析的方法签名，返回参数结构不会体现 ResponseData" )
public class ResponseEnforcerController {

    @GetMapping(value = "", produces = {"application/json"})
    @ApiOperation(value = "强制 ResponseData 结构", notes = "默认所有返回结构会被放在 ResponseData.data 中返回")
    public User normal() {
        return (new User("username", "password"));
    }

    @GetMapping(value = "/string", produces = {"application/json"})
    @ApiOperation(value = "string 返回值", notes = "直接返回 String 类型，需要明确标注 produces = {\"application/json\"}, 否则不能返回正确的 Content-Type")
    public String stringResponse() {
        return "a raw string";
    }

    @GetMapping(value = "/exclude-type")
    @ApiOperation(value = "类型排除", notes = "添加到 ziroom.web.response.exclude-types 中的类型不会被强制使用 ResponseData 结构")
    public ExcludedResponse excludeType() {
        ExcludedResponse response = new ExcludedResponse();
        response.setMsg("hello exclude type");
        return response;
    }

    @GetMapping(value = "/exclude-path")
    @ApiOperation(value = "路径排除", notes = "添加到 ziroom.web.response.exclude-paths 中的路径不会被强制使用 ResponseData 结构")
    public Map<String, String> excludePath() {
        return MapUtil.of("a", "b");
    }
}
