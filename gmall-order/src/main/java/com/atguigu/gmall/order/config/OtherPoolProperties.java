package com.atguigu.gmall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author kaixuan
 * @version 1.0
 * @date 3/4/2020 下午1:39
 */
@Data
@Configuration
@ConfigurationProperties(prefix ="gmall.other.pool")
public class OtherPoolProperties {

    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Long keepAliveTime;
    private Integer queueSize;
}
