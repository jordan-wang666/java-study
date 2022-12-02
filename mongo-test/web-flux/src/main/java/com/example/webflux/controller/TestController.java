package com.example.webflux.controller;

import com.example.webflux.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/test")
public class TestController {

    @Autowired
    private AssetRepository assetRepository;


    @GetMapping
    public Mono<ResponseEntity<Void>> hello() {
        return Mono.create(item -> {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                list.add("Hello" + i);
            }
            return Re;
        });
    }
}
