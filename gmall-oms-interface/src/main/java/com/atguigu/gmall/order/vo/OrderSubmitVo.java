package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 7/4/2020 下午3:52
 */
@Data
public class OrderSubmitVo {

    private String orderToken;
    private MemberReceiveAddressEntity address;
    private Integer payType;
    private String deliveryCompany;
    private List<OrderItemVo> itemVos;
    private Integer bounds;
    private Long userId;
    private BigDecimal totalPrice;



}
