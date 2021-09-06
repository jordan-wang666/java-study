package com.taco.cloud.controller;

import com.taco.cloud.config.MyLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @MyLog("test")
    @GetMapping("/hello")
    public String hello(String text) {
        return text;
    }
}
