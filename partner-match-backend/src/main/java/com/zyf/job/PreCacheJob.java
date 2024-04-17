package com.zyf.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyf.model.domain.User;
import com.zyf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(6L);

    /**
     *  每天执行，预热推荐用户
     */
    @Scheduled(cron = "0 37 16 * * *")
    public void doCacheRecommendUsers() {
        RLock rLock = redissonClient.getLock("partner-match:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                for (Long userId : mainUserList) {
                    Page<User> userPage = userService.page(new Page<>(1, 8));
                    String redisKey = String.format("partner-match:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error");
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUsers error", e);
        } finally {
            // 只能释放自己的锁
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
