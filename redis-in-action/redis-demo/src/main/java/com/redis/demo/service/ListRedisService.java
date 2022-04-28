package com.redis.demo.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class ListRedisService {

    public static final String KEY = "Redis-Demo-List-Key";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Command `RPUSH`
     * RPUSHkey-namevalue[value...]—Pushes the value(s) onto the right end of the list
     *
     * @param values values
     * @return Long
     */
    public Long rPush(String[] values) {
        return redisTemplate.opsForList().rightPushAll(KEY, values);
    }

    /**
     * Command `LPUSH`
     * LPUSH key-namevalue[value...]—Pushes the value(s) onto the left end of the list
     *
     * @param values values
     * @return Long
     */
    public Long lPush(String[] values) {
        return redisTemplate.opsForList().leftPushAll(KEY, values);
    }

    /**
     * Command `RPOP`
     * RPOP key-name—Removes and returns the rightmost item from the list
     *
     * @return Object
     */
    public Object rPop() {
        return redisTemplate.opsForList().rightPop(KEY);
    }

    /**
     * Command `LPOP`
     * LPOP key-name—Removes and returns the leftmost item from the list
     *
     * @return Object
     */
    public Object lPop() {
        return redisTemplate.opsForList().leftPop(KEY);
    }

    /**
     * Command `LINDEX`
     * LINDEX key-name offset—Returns the item at the given offset
     *
     * @return Object
     */
    public Object lIndex(Long index) {
        return redisTemplate.opsForList().index(KEY, index);
    }

    /**
     * Command `LRANGE`
     * LRANGE key-namestartend—Returns the items in the list at the offsets from start to end, inclusive
     *
     * @return Object
     */
    public Object lRange(long start, long end) {
        return redisTemplate.opsForList().range(KEY, start, end);
    }

    /**
     * Command `LTRIM`
     * LTRIM key-namestartend—Trims the list to only include items at indices between start and end, inclusive
     *
     * @param start start
     * @param end   end
     */
    public void lTrim(long start, long end) {
        redisTemplate.opsForList().trim(KEY, start, end);
    }

    /**
     * Command `BLPOP`
     * BLPOP key-name [key-name ...] timeout—Pops the leftmost item from the first non-empty LIST, or waits the timeout in seconds for an item
     *
     * @param seconds seconds
     * @return Object
     */
    public Object blPop(long seconds) {
        return redisTemplate.opsForList().leftPop(KEY, seconds, TimeUnit.SECONDS);
    }

    /**
     * Command `BRPOP`
     * BRPOP key-name [key-name ...] timeout—Pops the rightmost item from the first non-empty LIST, or waits the timeout in seconds for an item
     *
     * @param seconds seconds
     * @return Object
     */
    public Object bRPop(long seconds) {
        return redisTemplate.opsForList().rightPop(KEY, seconds, TimeUnit.SECONDS);
    }

    /**
     * Command `POPLPUSH`
     * RPOPLPUSH source-key dest-key—Pops the rightmost item from the source and LPUSHes the item to the destination, also returning the item to the user
     *
     * @return Object
     */
    public Object rPopLPush() {
        return redisTemplate.opsForList().rightPopAndLeftPush(KEY, "list-key");
    }

    /**
     * Command `BRPOPLPUSH`
     * RPOPLPUSH source-key dest-key—Pops the rightmost item from the source and LPUSHes the item to the destination, also returning the item to the user
     *
     * @return
     */
    public Object bRPopLPush() {
        return redisTemplate.opsForList().rightPopAndLeftPush(KEY, "list-key");
    }
}
