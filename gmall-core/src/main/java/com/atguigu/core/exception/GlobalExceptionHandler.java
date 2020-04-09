package com.atguigu.core.exception;


import com.atguigu.core.bean.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author kaixuan
 * @version 1.0
 * @date 7/4/2020 上午10:54
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public Resp error(Exception e) {
        return Resp.fail("系统繁忙，请稍后再试");
    }



    @ExceptionHandler(OrderException.class)
    public Resp error(OrderException e) {
        return Resp.fail(e.getMessage());
    }

}
