package com.zyf.spring;
import java.util.Date;

import com.zyf.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class SpringDataRedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void testRedisTemplate() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("zyfString", "Hello, Redis");
        valueOperations.set("zyfInteger", 20);
        valueOperations.set("zyfDouble", 1.0);
        User user = new User();
        user.setUsername("zyf");
        user.setUserAccount("266520");
        valueOperations.set("zyfUser", user);
        Assertions.assertEquals("Hello, Redis", valueOperations.get("zyfString"));
        Assertions.assertEquals(20, valueOperations.get("zyfInteger"));
        Assertions.assertEquals(1.0, valueOperations.get("zyfDouble"));
        System.out.println(valueOperations.get("zyfUser"));
    }
}
