package com.exe.carenest.authorizeservice.userManagement.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.stereotype.Service;

    import java.util.concurrent.TimeUnit;

@Service
    public class RedisService {

        @Autowired
        private RedisTemplate<String, Object> redisTemplate;

        public void save(String key, Object value, long expire,  TimeUnit timeUnit) {
            redisTemplate.opsForValue().set(key, value,expire,timeUnit);
        }

        public Object get(String key) {
            return redisTemplate.opsForValue().get(key);
        }

        public void delete(String key) {
            redisTemplate.delete(key);
        }
    }