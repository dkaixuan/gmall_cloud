package com.atguigu.gmall.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author kaixuan
 * @version 1.0
 * @date 25/3/2020 下午12:56
 */
@Configuration
public class CorsConfig {

    @Value("${corsConfig.allowedOrigin}")
    private String allowedOrigin;
    @Value("${corsConfig.allowedHeader}")
    private String allowedHeader;
    @Value("${corsConfig.allowedMethod}")
    private String allowedMethod;
    @Value("${corsConfig.allowCredentials}")
    private Boolean allowCredentials;


    /**
     * spring-webmvc：CorsFilter
     * spring-webflux：CorsWebFilter
     * springcloud-gateway集成的是webflux，所以这里使用的是CorsWebFilter
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        // 初始化CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin(allowedOrigin);
        // 允许的头信息
        config.addAllowedHeader(allowedHeader);
        // 允许的请求方式
        config.addAllowedMethod(allowedMethod);
        // 是否允许携带Cookie信息
        config.setAllowCredentials(allowCredentials);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
