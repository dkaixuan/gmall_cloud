package com.atguigu.gmall.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @author kaixuan
 * @version 1.0
 * @date 4/4/2020 下午2:30
 */
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object>{


    @Override
    public GatewayFilter apply(Object config) {
        return new AuthGatewayFilter();
    }





}
