package com.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ListRedisServiceTest {

    @Resource
    private ListRedisService listRedisService;

    @Test
    @DisplayName("Command Lists")
    void command() {
        Long rPush = listRedisService.rPush(new String[]{"Apple", "Pear", "Orange", "Tomato"});
        log.info("After rpush:{},data:{}", rPush, listRedisService.lRange(0, 100));
        Long lPush = listRedisService.lPush(new String[]{"Banana", "Cherry", "Strawberry"});
        log.info("After lpush:{},data:{}", lPush, listRedisService.lRange(0, 100));
        Object rPop = listRedisService.rPop();
        log.info("After rPop:{},data:{}", rPop, listRedisService.lRange(0, 100));
        Object lPop = listRedisService.lPop();
        log.info("After lPop:{},data:{}", lPop, listRedisService.lRange(0, 100));
        Object o = listRedisService.lIndex(1L);
        log.info("Get index 1:{}", o);
        listRedisService.lTrim(2, 3);
        log.info("Finally list:{}", listRedisService.lRange(0, 100));
    }

    @Test
    @DisplayName("Command Lists Block")
    void blockCommand() {
        Long rPush = listRedisService.rPush(new String[]{"Apple", "Pear", "Orange", "Tomato"});
        log.info("After rpush:{},data:{}", rPush, listRedisService.lRange(0, 100));
        Long lPush = listRedisService.lPush(new String[]{"Banana", "Cherry", "Strawberry"});
        log.info("After lpush:{},data:{}", lPush, listRedisService.lRange(0, 100));

        Object o1 = listRedisService.blPop(2L);
        log.info("After blPop:{},data:{}", o1, listRedisService.lRange(0, 100));
        Object o2 = listRedisService.bRPop(2L);
        log.info("After blPop:{},data:{}", o2, listRedisService.lRange(0, 100));

        Object o3 = listRedisService.rPopLPush();
        log.info("After blPop:{},data:{}", o3, listRedisService.lRange(0, 100));
        Object o4 = listRedisService.bRPopLPush();
        log.info("After blPop:{},data:{}", o4, listRedisService.lRange(0, 100));
    }
}