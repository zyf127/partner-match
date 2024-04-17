package com.zyf.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;

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

    @Test
    public void testWatchDog() {
        RLock rLock = redissonClient.getLock("partner-match:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("线程" + Thread.currentThread().getName() + "拿到了锁！");
                String redisKey = String.format("partner-match:user:recommend:%s", 100);
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                valueOperations.set(redisKey, "watch dog");
                Thread.sleep(300000);
            }
        } catch (InterruptedException e) {

        } finally {
            // 只能释放自己的锁
            if (rLock.isHeldByCurrentThread()) {
                System.out.println("线程" + Thread.currentThread().getName() + "释放了锁！");
                rLock.unlock();
            }
        }
    }

}
