package com.atguigu.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author kaixuan
 * @version 1.0
 * @date 7/4/2020 下午2:25
 */
@EnableFeignClients
@SpringBootApplication
public class GmallOrderApplication {

    public static void main(String args[]) {
        SpringApplication.run(GmallOrderApplication.class,args);
    }
}
