package com.redis.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@SpringBootTest
class SetRedisServiceTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void oneSet() {
        Long add = redisTemplate.opsForSet().add("set-key", "a", "b", "c", "e", "f", "g"); // command sadd
        log.info("After add result:{}", add);
        Set<Object> members1 = redisTemplate.opsForSet().members("set-key"); // command smembers
        log.info("After add members:{}", members1);
        Long remove = redisTemplate.opsForSet().remove("set-key", "c", "d");// command srem
        log.info("After remove result:{}", remove);
        Set<Object> members2 = redisTemplate.opsForSet().members("set-key"); // command smembers
        log.info("After remove members:{}", members2);
        Long removeAgain = redisTemplate.opsForSet().remove("set-key", "c", "d");// command srem
        log.info("After remove again result:{}", removeAgain);
        Set<Object> members3 = redisTemplate.opsForSet().members("set-key"); // command smembers
        log.info("After remove members:{}", members3);
        Long size = redisTemplate.opsForSet().size("set-key"); // command scard
        log.info("set-key size:{}", size);
        Boolean moveFlag1 = redisTemplate.opsForSet().move("set-key", "a", "set-key2"); // command smove
        log.info("Move 'a' from set-key to set-key2 result:{}", moveFlag1);
        Boolean moveFlag2 = redisTemplate.opsForSet().move("set-key", "c", "set-key2"); // command smove
        log.info("Move 'c' from set-key to set-key2 result:{}", moveFlag2);
        Set<Object> members4 = redisTemplate.opsForSet().members("set-key"); // command smembers
        log.info("Finally set-key members:{}", members4);
        Set<Object> members5 = redisTemplate.opsForSet().members("set-key2"); // command smembers
        log.info("Finally set-key2-key members:{}", members5);
        Object pop = redisTemplate.opsForSet().pop("set-key"); // command spop
        Set<Object> members6 = redisTemplate.opsForSet().members("set-key"); // command smembers
        log.info("After pop result:{} set-key1 value:{}", pop, members6);
    }

    @Test
    void manySet() {
        Long add1 = redisTemplate.opsForSet().add("skey1", "a", "b", "c", "d");
        Set<Object> skey1 = redisTemplate.opsForSet().members("skey1");
        log.info("Start add to skey1 result:{} member:{}", add1, skey1);
        Long add2 = redisTemplate.opsForSet().add("skey2", "c", "d", "e", "f");
        Set<Object> skey2 = redisTemplate.opsForSet().members("skey2");
        log.info("Start add to skey2 result:{} member:{}", add2, skey2);
        Set<Object> difference = redisTemplate.opsForSet().difference("skey1", "skey2");// command diff return first set
        log.info("Difference between skey1&skey2:{}", difference);
        Set<Object> intersect = redisTemplate.opsForSet().intersect("skey1", "skey2");// command sinter
        log.info("Intersect between skey1&skey2:{}", intersect);
        Set<Object> union = redisTemplate.opsForSet().union("skey1", "skey2");// command sunion
        log.info("Union between skey1&skey2:{}", union);
    }
}
