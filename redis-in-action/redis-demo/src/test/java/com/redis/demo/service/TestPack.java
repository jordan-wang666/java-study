package com.redis.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class TestPack {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void publishTestChannel() {
        for (int i = 1; i <= 10; i++) {
            System.out.println(i);
            redisTemplate.convertAndSend("TestChannel", String.valueOf(i));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
