package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kaixuan
 * @version 1.0
 * @date 9/4/2020 下午9:38
 */
@Configuration
public class RabbitMqConfig {
    private static final String ORDER_DELAY_EXCHANGE="user.order.delay.exchange";
    private static final String WMS_DELAY_QUEUE="user.wms.delay.queue";
    private static final String WMS_DELAY_ROUTING_KEY="stock.delay";
    private static final String ORDER_DEAD_EXCHANGE="user.order.exchange";
    private static final String USER_WMS_ORDER_QUEUE = "user.wms.queue";

    private static final String ORDER_DEAD_ROUTING_KEY="order";


    @Bean
    public Exchange exchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE,true, false);
    }

    @Bean
    public Queue delayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        //所有消息的过期时间
        arguments.put("x-message-ttl", 10*1000);
        //指定死信发给哪个交换机
        arguments.put("x-dead-letter-exchange",ORDER_DEAD_EXCHANGE);
        //死信发出去的路由键
        arguments.put("x-dead-letter-routing-key", ORDER_DEAD_ROUTING_KEY);
        return new Queue(WMS_DELAY_QUEUE, true,
                false, false, null);
    }

    @Bean
    public Binding delayBinding() {
        return new Binding(WMS_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                ORDER_DELAY_EXCHANGE, WMS_DELAY_ROUTING_KEY,
                null);
    }

    @Bean
    public Exchange deadExchange() {
        return new DirectExchange(ORDER_DEAD_EXCHANGE, true, false);
    }



    @Bean
    public Queue queue() {
        return new Queue(USER_WMS_ORDER_QUEUE, true, false, false,null);
    }


    @Bean
    public Binding deadBinding() {
        return new Binding(USER_WMS_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                ORDER_DEAD_EXCHANGE, "stock:unlock", null);


    }





}
