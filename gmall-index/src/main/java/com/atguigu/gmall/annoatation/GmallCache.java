package com.atguigu.gmall.annoatation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author kaixuan
 * @version 1.0
 * @date 1/4/2020 下午9:46
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    // @AliasFor 等价
    @AliasFor("prefix")
    String value() default "";


    @AliasFor("value")
    String prefix() default "";

    /**
     * 缓存过期时间
     * 以分钟为单位
     * @return
     */
    int timeout() default 5;

    /**
     * 防止缓存雪崩指定的随机值范围
     * @return
     */
    int random() default 5;
}
