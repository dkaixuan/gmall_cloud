package com.atguigu.gmall.order.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.order.entity.OrderEntity;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:20
 */
public interface GmallOmsApi {

    @PostMapping("oms/order")
    Resp<OrderEntity> saveOrder(@RequestBody OrderSubmitVo orderSubmitVo);
}
