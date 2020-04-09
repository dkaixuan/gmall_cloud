package com.atguigu.gmall.order.service;

import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:06
 */
public interface OrderService {
    OrderConfirmVo confirm();

    void submit(OrderSubmitVo orderSubmitVo);
}
