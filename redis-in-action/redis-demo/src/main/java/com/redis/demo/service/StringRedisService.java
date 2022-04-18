package com.redis.demo.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StringRedisService {
    public static final String KEY = "Redis-Demo-Key";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Command `SET`
     */
    public void add(String value) {
        stringRedisTemplate.opsForValue().set(KEY, value);
    }

    /**
     * Command `GET`
     */
    public String get() {
        return stringRedisTemplate.opsForValue().get(KEY);
    }

    /**
     * Command `INCR`
     * INCR key-name—Increments the value stored at the key by 1
     */
    public Long incr() {
        return stringRedisTemplate.opsForValue().increment(KEY);
    }

    /**
     * Command 'DECR'
     * DECR key-name—Decrements the value stored at the key by 1
     */
    public Long decr() {
        return stringRedisTemplate.opsForValue().decrement(KEY);
    }

    /**
     * Command 'INCRBY'
     * INCRBY key-name amount—Increments the value stored at the key by the provided integer value
     */
    public Long incrBy(int delta) {
        return stringRedisTemplate.opsForValue().increment(KEY, delta);
    }

    /**
     * Command 'DECRBY'
     * DECRBY key-name amount—Decrements the value stored at the key by the provided integer value
     */
    public Long decrBy(int delta) {
        return stringRedisTemplate.opsForValue().decrement(KEY, delta);
    }

    /**
     * Command 'INCRBYFLOAT'
     * INCRBYFLOAT key-name amount—Increments the value stored at the key by the provided float value (available in Redis 2.6 and later)
     */
    public Double incrByFloat(double delta) {
        return stringRedisTemplate.opsForValue().increment(KEY, delta);
    }

    /**
     * Command 'APPEND'
     * APPEND key-name value—Concatenates the provided value to the string already stored at the given key
     *
     * @param value value
     */
    public void append(String value) {
        stringRedisTemplate.opsForValue().append(KEY, value);
    }

    /**
     * Command 'GETRANGE'
     * GETRANGE key-name start end—Fetches the substring, including all charac- ters from the start offset to the end offset, inclusive
     *
     * @param start start
     * @param end   end
     * @return string
     */
    public String getRange(long start, long end) {
        return stringRedisTemplate.opsForValue().get(KEY, start, end);
    }

    /**
     * Command 'GETBIT'
     * GETBIT key-name offset—Treats the byte string as a bit string, and returns the value of the bit in the string at the provided bit offset
     *
     * @param offset offset
     * @return Boolean
     */
    public Boolean getBit(long offset) {
        return stringRedisTemplate.opsForValue().getBit(KEY, offset);
    }

    /**
     * Command 'SETBIT'
     * SETBIT key-name offset value—Treats the byte string as a bit string, and sets the value of the bit in the string at the provided bit offset
     *
     * @param offset offset
     * @param value  value
     * @return boolean
     */
    public Boolean setBit(long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(KEY, offset, value);
    }
}
