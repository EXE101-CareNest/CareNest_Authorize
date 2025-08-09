package com.exe.carenest.authorizeservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean isExpired(String key) {
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        // -2 means key does not exist (expired or deleted)
        return  expire == -2;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}