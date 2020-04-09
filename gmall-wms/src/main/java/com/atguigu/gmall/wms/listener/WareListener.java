package com.atguigu.gmall.wms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 9/4/2020 上午11:02
 */
@Component
public class WareListener {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private WareSkuDao wareSkuDao;
    private static final String ORDER_TOKEN_PREFIX = "order:token:";


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "WMS-UNLOCK-QUEUE",durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"stock:unlock"}
    ))



    public void unLockListener(String orderToken) {
        String lockJson = stringRedisTemplate.opsForValue().get(ORDER_TOKEN_PREFIX + orderToken);
        List<SkuLockVo> lockVos = JSON.parseArray(lockJson, SkuLockVo.class);
        lockVos.forEach(skuLockVo -> {
            wareSkuDao.unLockStore(skuLockVo.getWareSkuId(), skuLockVo.getCount());
        });

    }


}
