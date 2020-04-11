package com.atguigu.gmall.order.dao;

import com.atguigu.gmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author kaixuan
 * @email 670973127@qq.com
 * @date 2020-04-06 15:04:34
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    int closeOrder(String orderToken);

}
