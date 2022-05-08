package com.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class HashRedisServiceTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String key = "hash-key";
    private final String key2 = "hash-key2";

    @Test
    void add() {
        Map<String, String> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        map.put("k3", "v3");
        // Save one key-value object in redis
        redisTemplate.opsForHash().putAll(key, map);
        log.info("After save hash-key:{}", redisTemplate.opsForHash().values(key));
        List<Object> keys = new ArrayList<>();
        keys.add("k2");
        keys.add("k3");
        log.info("Command hmget :{}", redisTemplate.opsForHash().multiGet(key, keys));
        log.info("Command hlen :{}", redisTemplate.opsForHash().size(key));
        log.info("Command hdel :{}", redisTemplate.opsForHash().delete(key, "k1", "k3"));
        log.info("After command hedl :{}", redisTemplate.opsForHash().values(key));
    }

    @Test
    void senior() {
        Map<String, String> map = new HashMap<>();
        map.put("short", "hello");
        map.put("long", "1000*'1'");
        map.put("k3", "v3");
        // Save one key-value object in redis
        redisTemplate.opsForHash().putAll(key2, map);
        log.info("After save hash-key2:{}", redisTemplate.opsForHash().keys(key2));
        log.info("Command hexists :{}", redisTemplate.opsForHash().hasKey(key2, "num"));
        log.info("Command hincrby :{}", redisTemplate.opsForHash().increment(key2, "num", 1));
        log.info("Command hexists :{}", redisTemplate.opsForHash().hasKey(key2, "num"));
        log.info("Command hget hash-key num :{}", redisTemplate.opsForHash().get(key2, "num"));
        log.info("Command hincrby :{}", redisTemplate.opsForHash().increment(key2, "num", 1.23));
        log.info("Command hget hash-key num :{}", redisTemplate.opsForHash().get(key2, "num"));
        log.info("After save hash-key2:{}", redisTemplate.opsForHash().values(key2));
    }
}
