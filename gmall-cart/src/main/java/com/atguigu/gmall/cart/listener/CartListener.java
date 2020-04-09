package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午2:10
 */
@Component
public class CartListener {
    private static final String PRICE_PREFIX = "cart:sku:";
    private static final String KEY_PREFIX = "user:cart:";
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value ="CART_ITEM_QUEUE",durable = "true"),
            exchange = @Exchange(value = "GMALL_PMS_EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.update"}
    ))
    public void listener(Long skuId) {
        Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsClient.querySkuById(skuId);
        SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
        BigDecimal price = skuInfoEntity.getPrice();
        System.out.println(price.toString());
        stringRedisTemplate.opsForValue().set(PRICE_PREFIX + skuId, price.toString());
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "CART_DELETE_QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"cart.delete"}
    ))


    public void deleteCartByUserIdAndSkuId(Map<String,Object>map) {
        Long userId= (Long) map.get("userId");
        List<Long> skuIds  = (List<Long>) map.get("skuIds");
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(KEY_PREFIX+userId);
            hashOperations.delete(skuIds.toArray());


    }







}
