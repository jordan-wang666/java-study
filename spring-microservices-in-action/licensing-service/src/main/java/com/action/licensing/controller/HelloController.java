package com.action.licensing.controller;

import com.action.licensing.model.License;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("hello")
    public String hello(License License) {
        return "param";
    }
}
