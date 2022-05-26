package com.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageReceive {

    public void getMessage(String object) {
        log.info(object);
    }
}
