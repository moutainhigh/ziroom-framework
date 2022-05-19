package com.ziroom.framework.example.controller;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/validation")
public class ValidationController {

    @Data
    public static class SimpleParam {
        @NotNull
        String requiredKey;
    }

    @Data
    public static class ComplexParam {
        @NotNull
        private User user;

        @Data
        public static class User {
            String name;
        }
    }

    @GetMapping("/validated")
    public String validated(@Validated SimpleParam param) {
        return "ok";
    }

    @PostMapping("/postJSON")
    public String validateObject(@RequestBody @Validated ComplexParam param) {
        return "";
    }

}
