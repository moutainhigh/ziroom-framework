package com.ziroom.framework.example.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/log")
public class AccessLogController {

    @PostMapping("/json")
    public void postJSON(@RequestBody Map<String, Object> payload) {
        return;
    }

    @GetMapping("/query")
    public void getQuery() {
        return;
    }

    @PostMapping("/form")
    public void postForm() {
        return;
    }
}
