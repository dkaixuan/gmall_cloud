package com.atguigu.gmall.search.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author kaixuan
 * @version 1.0
 * @date 1/4/2020 上午11:53
 */
@Component
public class GoodsListener {


    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(value ="gmall-search-queue",durable = "true"),
            exchange =@Exchange(value ="GMALL-PMS-EXCHANGE",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions ="true"),
            key = {"item.insert","item.update"}
    ))

    public void listener(Long spuId) {

    }

}
