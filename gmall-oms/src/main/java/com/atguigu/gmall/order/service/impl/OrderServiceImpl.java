package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.order.dao.OrderDao;
import com.atguigu.gmall.order.dao.OrderItemDao;
import com.atguigu.gmall.order.entity.OrderEntity;
import com.atguigu.gmall.order.entity.OrderItemEntity;
import com.atguigu.gmall.order.feign.GmallPmsClient;
import com.atguigu.gmall.order.feign.GmallUmsClient;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderItemVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private GmallUmsClient gmallUmsClient;
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private AmqpTemplate amqpTemplate;
    private static final String ORDER_DELAY_EXCHANGE="user.order.delay.exchange";
    private static final String ORDER_DELAY_ROUTING_KEY="order.delay";
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageVo(page);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderEntity saveOrder(OrderSubmitVo orderSubmitVo) {
        MemberReceiveAddressEntity address = orderSubmitVo.getAddress();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        Resp<MemberEntity> memberEntityResp =
                gmallUmsClient.queryMemberById(orderSubmitVo.getUserId());
        MemberEntity memberEntity = memberEntityResp.getData();
        orderEntity.setMemberId(memberEntity.getId());
        orderEntity.setMemberUsername(memberEntity.getUsername());

        orderEntity.setModifyTime(orderEntity.getCreateTime());
        orderEntity.setDeliveryCompany(orderSubmitVo.getDeliveryCompany());

        orderEntity.setTotalAmount(orderSubmitVo.getTotalPrice());
        orderEntity.setOrderSn(orderSubmitVo.getOrderToken());

        this.save(orderEntity);

        Long id = orderEntity.getId();
        List<OrderItemVo> itemVos =
                orderSubmitVo.getItemVos();

        itemVos.forEach(item->{
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setSkuId(id);
            orderItemEntity.setSkuAttrsVals(JSON.toJSONString(item.getSaleAttrValues()));
            orderItemDao.insert(orderItemEntity);
        });

        //发送延时消息
        amqpTemplate.convertAndSend(ORDER_DELAY_EXCHANGE, ORDER_DELAY_ROUTING_KEY, orderSubmitVo.getOrderToken());



        return orderEntity;
    }

}