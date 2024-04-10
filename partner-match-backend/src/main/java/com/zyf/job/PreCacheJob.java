package com.zyf.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyf.domain.User;
import com.zyf.service.UserService;
import lombok.extern.slf4j.Slf4j;
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

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(6L);

    /**
     *  每天执行，预热推荐用户
     */
    @Scheduled(cron = "0 37 16 * * *")
    public void doCacheRecommendUsers() {
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
}
