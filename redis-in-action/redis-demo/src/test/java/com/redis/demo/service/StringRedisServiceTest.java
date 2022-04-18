package com.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class StringRedisServiceTest {

    @Resource
    private StringRedisService service;

    @Test
    void testNumber() {
        service.add("10");
        String value1 = service.get();
        log.info("Method get [Get value from redis string:{}]", value1);
        Long increment = service.incr();
        log.info("Method incr plus 1 [increment:{}]", increment);
        String value2 = service.get();
        log.info("Method get [Get value from redis string:{}]", value2);
        Long decrement = service.decr();
        log.info("Method decr minus 1 [decrement:{}]", decrement);
        String value3 = service.get();
        log.info("Method get [Get value from redis string:{}]", value3);
        Long incrBy = service.incrBy(10);
        log.info("Method incr plus 10 [increment:{}]", incrBy);
        String value4 = service.get();
        log.info("Method get [Get value from redis string:{}]", value4);
        Long decrBy = service.decrBy(5);
        log.info("Method decr minus 5 [decrement:{}]", decrBy);
        String value5 = service.get();
        log.info("Method get [Get value from redis string:{}]", value5);
        Double incrByFloat = service.incrByFloat(2.6);
        log.info("Method incr plus 2.6 [increment:{}]", incrByFloat);
        String value6 = service.get();
        log.info("Method get [Get value from redis string:{}]", value6);
    }

    @Test
    void testString() {
        service.add("HELLO");
        String value1 = service.get();
        log.info("Method get [Get value from redis string:{}]", value1);
        service.append("WORLD");
        String value2 = service.get();
        log.info("Method get [Get value from redis string:{}]", value2);
        String range = service.getRange(2, 5);
        log.info("Get range:{}", range);
        String value3 = service.get();
        log.info("Method get [Get value from redis string:{}]", value3);
        Boolean bit = service.getBit(5);
        log.info("Get bit:{}", bit);
        String value4 = service.get();
        log.info("Method get [Get value from redis string:{}]", value4);
        Boolean setBit = service.setBit(5, false);
        log.info("Set bit:{}", setBit);
        String value5 = service.get();
        log.info("Method get [Get value from redis string:{}]", value5);
    }
}