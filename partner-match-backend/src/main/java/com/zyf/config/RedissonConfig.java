package com.zyf.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;

    private int port;

    @Bean
    public RedissonClient redissonClient() {
        // 1. Create config object
        String redisAddress = String.format("redis://%s:%s", host, port);
        Config config = new Config();
        config.useSingleServer().setAddress(redisAddress).setDatabase(1);
        // 2. Create Redisson instance
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
