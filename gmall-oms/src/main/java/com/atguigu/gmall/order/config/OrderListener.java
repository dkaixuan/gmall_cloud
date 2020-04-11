package com.atguigu.gmall.order.config;

import com.atguigu.gmall.order.dao.OrderDao;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author kaixuan
 * @version 1.0
 * @date 10/4/2020 下午4:31
 */
@Component
public class OrderListener {
    private static final String USER_ORDER_QUEUE = "user.order.queue";
    private static final String ORDER_EXCHANGE="GMALL-ORDER-EXCHANGE";
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private OrderDao orderDao;


    @RabbitListener(queues = {USER_ORDER_QUEUE})
    public void closeOrder(String orderToken) {
       int influenceCount= orderDao.closeOrder(orderToken);
        //执行了关单操作，更新成功，解锁库存
        if (influenceCount == 1) {
            amqpTemplate.convertAndSend(ORDER_EXCHANGE, "stock:unlock", orderToken);
        }

    }



}
