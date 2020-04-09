package com.atguigu.gmall.wms.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午30
 */
@Component
public class RedissonConfig {
    @Bean
    public RedissonClient redisClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://114.55.36.227:6379");
        return Redisson.create(config);
    }
}
