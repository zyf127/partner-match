package com.zyf.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Test
    public void testRedisson() {
        RList<Object> rList = redissonClient.getList("test-list");
//        rList.add("zyf");
//        System.out.println(rList.get(0));
//        rList.remove(0);
        RMap<Object, Object> rMap = redissonClient.getMap("test-map");
//        rMap.put("student", "zhangsan");
        rMap.remove("student");
    }
}
