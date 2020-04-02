package com.atguigu.gmall.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annoatation.GmallCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午12:41
 */
@Slf4j
@Aspect
@Component
public class CacheAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @Around("@annotation(com.atguigu.gmall.annoatation.GmallCache)")
    public Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取目标方法的注解参数
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        //方法前缀
        String prefix = annotation.prefix();
        //过期时间
        int timeout = annotation.timeout();
        //获取随机值范围
        int random = annotation.random();
        //获取目标方法的参数
        Object[] args = joinPoint.getArgs();
        Object argId = args[0];
        //获取目标对象的返回值
        Class<?> returnType = method.getReturnType();
        //拼接key
        String key = prefix + argId;
        Object result=null;
        //查询缓存
        result = this.getFromCache(key, returnType);
        if (result != null) {
            return result;
        }

        //查询缓存为空则加锁
        RLock lock = redissonClient.getLock("lock:" + argId);
        lock.lock(timeout, TimeUnit.MINUTES);
        try {
            //加完锁再访问一下缓存
            result = this.getFromCache(key, returnType);
            if (result != null) {
                return result;
            }
            //执行目标方法访问数据库
            result = joinPoint.proceed(args);
            //将返回结果放到数据库
            stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(result), (int) (timeout*Math.random()*random),TimeUnit.MINUTES);
        }finally {
            lock.unlock();
        }
        return result;
    }


    private Object getFromCache(String key, Class<?> returnType) {
        String results= stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(results)){
            return JSON.parseObject(results,returnType);
        }
        return null;
    }






}
