package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午3:52
 */
@Data
public class OrderConfirmVo {

    List<MemberReceiveAddressEntity> addresses;
    List<OrderItemVo> orderItems;
    private Integer bounds;
    private BigDecimal totalPrice;
    private String orderToken;

}
