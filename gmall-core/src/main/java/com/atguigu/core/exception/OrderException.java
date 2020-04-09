package com.atguigu.core.exception;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午10:16
 */
public class OrderException extends RuntimeException {

    public OrderException() {
        super();
    }

    public OrderException(String message) {
        super(message);
    }
}
