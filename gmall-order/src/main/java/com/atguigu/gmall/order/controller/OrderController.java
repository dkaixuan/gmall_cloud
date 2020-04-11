package com.atguigu.gmall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.order.pay.AlipayTemplate;
import com.atguigu.gmall.order.pay.PayVo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:04
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @GetMapping("confirm")
    public Resp<OrderConfirmVo> confirm() {
        OrderConfirmVo orderConfirmVo = orderService.confirm();
        return Resp.ok(orderConfirmVo);
    }

    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVo orderSubmitVo) throws AlipayApiException {
        orderService.submit(orderSubmitVo);
        //TODO
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no("");

        alipayTemplate.pay(payVo);

        return Resp.ok(null);
    }



    @PostMapping("pay/success")
    public Resp<Object> paySuccess() {

        return null;
    }



}
