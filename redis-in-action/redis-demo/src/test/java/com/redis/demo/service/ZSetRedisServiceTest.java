package com.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@SpringBootTest
public class ZSetRedisServiceTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String key = "zset-key";

    @Test
    void regular() {
        Set<ZSetOperations.TypedTuple<Object>> set = new LinkedHashSet<>();
        set.add(new DefaultTypedTuple<>("a", 3.0));
        set.add(new DefaultTypedTuple<>("b", 2.0));
        set.add(new DefaultTypedTuple<>("c", 1.0));
        log.info("Command zadd :{}", redisTemplate.opsForZSet().add(key, set));
        log.info("Command zcard :{}", redisTemplate.opsForZSet().zCard(key));
        log.info("Command zincrby :{}", redisTemplate.opsForZSet().incrementScore(key, "c", 3.0));
        log.info("Command zscore :{}", redisTemplate.opsForZSet().score(key, "b"));
        log.info("Command zrank :{}", redisTemplate.opsForZSet().rank(key, "c"));
        log.info("Command zcount :{}", redisTemplate.opsForZSet().count(key, 0.0, 3.0));
        log.info("Command zrem :{}", redisTemplate.opsForZSet().remove(key, "b"));
        log.info("Command zrange :{}", redisTemplate.opsForZSet().rangeWithScores(key, 0, -1));
    }

    @Test
    void senior() {
        Set<ZSetOperations.TypedTuple<Object>> set1 = new LinkedHashSet<>();
        set1.add(new DefaultTypedTuple<>("a", 1.0));
        set1.add(new DefaultTypedTuple<>("b", 2.0));
        set1.add(new DefaultTypedTuple<>("c", 3.0));
        log.info("Command zadd [zset-1]:{}", redisTemplate.opsForZSet().add("zset-1", set1));
        Set<ZSetOperations.TypedTuple<Object>> set2 = new LinkedHashSet<>();
        set2.add(new DefaultTypedTuple<>("b", 4.0));
        set2.add(new DefaultTypedTuple<>("c", 1.0));
        set2.add(new DefaultTypedTuple<>("d", 0.0));
        log.info("Command zadd [zset-2]:{}", redisTemplate.opsForZSet().add("zset-2", set2));
        log.info("Command zinterstore [zset-i]:{}", redisTemplate.opsForZSet().intersectAndStore("zset-1", "zset-2", "zset-i"));
        log.info("Command zrange [zset-i]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-i", 0, -1));
        log.info("Command zunionstore [zset-u]:{}", redisTemplate.opsForZSet().unionAndStore("zset-1", Collections.singleton("zset-2"), "zset-u", RedisZSetCommands.Aggregate.MIN));
        log.info("Command zrange [zset-u]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-u", 0, -1));
        log.info("Command sadd :{}", redisTemplate.opsForSet().add("set-1", "a", "d"));
        List<String> list = new ArrayList<>();
        list.add("zset-2");
        list.add("set-1");
        log.info("Command zunionstore [zset-u]:{}", redisTemplate.opsForZSet().unionAndStore("zset-1", list, "zset-u2"));
        log.info("Command zrange [zset-u2]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-u2", 0, -1));
        log.info("[zset-1]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-1", 0, -1));
        log.info("[zset-2]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-1", 0, -1));
        log.info("[zset-i]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-i", 0, -1));
        log.info("[zset-u]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-u", 0, -1));
        log.info("[set-1]:{}", redisTemplate.opsForSet().members("set-1"));
        log.info("[zset-u2]:{}", redisTemplate.opsForZSet().rangeWithScores("zset-u2", 0, -1));
    }
}
