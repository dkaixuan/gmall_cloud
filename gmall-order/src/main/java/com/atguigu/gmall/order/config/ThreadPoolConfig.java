package com.atguigu.gmall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kaixuan
 * @version 1.0
 * @date 3/4/2020 下午1:35
 */
@Configuration
public class ThreadPoolConfig {
    /*
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler
     */

    /**
     * 核心业务线程池
     *
     * @param poolProperties
     * @return
     */
    @Bean("mainThreadPoolExecutor")
    public ThreadPoolExecutor threadPoolExecutor(MainPoolProperties poolProperties) {

        LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<>(poolProperties.getQueueSize());

        return new ThreadPoolExecutor(poolProperties.getCorePoolSize(), poolProperties.getMaximumPoolSize(), poolProperties.getKeepAliveTime(),
                TimeUnit.MINUTES, deque);
    }


    /**
     * 非核心业务线程池
     *
     * @param poolProperties
     * @return
     */
    @Bean("otherThreadPoolExecutor")
    public ThreadPoolExecutor otherThreadPoolExecutor(OtherPoolProperties poolProperties) {
        LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<>(poolProperties.getQueueSize());
        return new ThreadPoolExecutor(poolProperties.getCorePoolSize(), poolProperties.getMaximumPoolSize(), poolProperties.getKeepAliveTime(),
                TimeUnit.MINUTES, deque);
    }

    }
