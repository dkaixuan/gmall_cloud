package com.atguigu.gmall.order.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.exception.OrderException;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.order.entity.OrderEntity;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.AuthInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderItemVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.sms.vo.SaleVo;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:06
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final String KEY_PREFIX = "user:cart:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GmallUmsClient umsClient;
    @Autowired
    private GmallCartClient cartClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private GmallOmsClient omsClient;
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String ORDER_EXCHANGE="GMALL-ORDER-EXCHANGE";
    private static final String ORDER_TOKEN_PREFIX = "order:token:";

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;




    @Override
    public OrderConfirmVo confirm() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        UserInfo userInfo = AuthInterceptor.getUserInfo();
        Long userId = userInfo.getId();
        if (userId == null) {
            return null;
        }


        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            //查询用户收货信息
            Resp<List<MemberReceiveAddressEntity>> addressResp = umsClient.queryAddressByUserId(userId);
            List<MemberReceiveAddressEntity> receiveAddressEntities = addressResp.getData();
            orderConfirmVo.setAddresses(receiveAddressEntities);
        }, threadPoolExecutor);


        CompletableFuture<List<Cart>> cartListCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //查询购物车选中的商品信息
            Resp<List<Cart>> cartResp = cartClient.queryCartsByUserId(userId);
            List<Cart> cartList = cartResp.getData();
            if (CollectionUtils.isEmpty(cartList)) {
                throw new OrderException("请勾选商品在提交订单");
            }
            return cartList;
        }, threadPoolExecutor);


        CompletableFuture<List<OrderItemVo>> OrderItemListCompletableFuture = cartListCompletableFuture.thenApplyAsync(cartList -> {
            AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(new BigDecimal("0"));

            List<OrderItemVo> orderItemVoList = cartList.stream().map(cart -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                Long skuId = cart.getSkuId();

                CompletableFuture<Void> skuFuture = CompletableFuture.runAsync(() -> {
                    Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(skuId);
                    SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                    if (skuInfoEntity != null) {
                        totalPrice.getAndAccumulate(skuInfoEntity.getPrice().multiply(new BigDecimal(cart.getCount())), BigDecimal::add);
                        totalPrice.get().add(skuInfoEntity.getPrice());
                        orderItemVo.setPrice(skuInfoEntity.getPrice());
                        orderItemVo.setTitle(skuInfoEntity.getSkuTitle());
                        orderItemVo.setWeight(skuInfoEntity.getWeight());
                        orderItemVo.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                    }
                }, threadPoolExecutor);

                CompletableFuture<Void> wareSkuFuture = CompletableFuture.runAsync(() -> {
                    orderItemVo.setSkuId(skuId);
                    orderItemVo.setCount(cart.getCount());
                    Resp<List<WareSkuEntity>> wareSkuEntityResp = wmsClient.listWareSkuEntity(skuId);
                    List<WareSkuEntity> wareSkuEntities = wareSkuEntityResp.getData();
                    if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                        orderItemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
                    }
                }, threadPoolExecutor);

                CompletableFuture<Void> skuSaleFuture = CompletableFuture.runAsync(() -> {
                    Resp<List<SaleVo>> salesResp = smsClient.querySkuSalesBySkuId(skuId);
                    List<SaleVo> saleVoList = salesResp.getData();
                    orderItemVo.setSales(saleVoList);
                }, threadPoolExecutor);
                CompletableFuture.allOf(skuFuture, wareSkuFuture, skuSaleFuture).join();
                return orderItemVo;
            }).collect(Collectors.toList());
            orderConfirmVo.setTotalPrice(totalPrice.get());
            return orderItemVoList;
        }, threadPoolExecutor);


        CompletableFuture<Void> BoundsFuture = OrderItemListCompletableFuture.thenAcceptAsync(orderItemVoList -> {
            //查询用户信息，获取积分
            Resp<MemberEntity> memberEntityResp = umsClient.queryMemberById(userId);
            MemberEntity memberEntity = memberEntityResp.getData();
            orderConfirmVo.setBounds(memberEntity.getIntegration());
            orderConfirmVo.setOrderItems(orderItemVoList);
        }, threadPoolExecutor);

        CompletableFuture.allOf(addressFuture, cartListCompletableFuture, BoundsFuture).join();

        //通过雪花算法生成唯一订单号
        String orderToken = IdWorker.getIdStr();
        orderConfirmVo.setOrderToken(orderToken);

        stringRedisTemplate.opsForValue().set(ORDER_TOKEN_PREFIX + orderToken, orderToken);

        return orderConfirmVo;
    }


    @Override
    public void submit(OrderSubmitVo orderSubmitVo) {
        String orderToken = orderSubmitVo.getOrderToken();
        //防止重复提交，实现接口幂等性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long flag = stringRedisTemplate.execute(new DefaultRedisScript<>(script,Long.class), Arrays.asList(ORDER_TOKEN_PREFIX + orderToken), orderToken);
        if (flag == 0) {
            throw new OrderException("订单不可重复提交");
        }
        //校验价格
        List<OrderItemVo> itemVos = orderSubmitVo.getItemVos();
        BigDecimal totalPrice = orderSubmitVo.getTotalPrice();
        BigDecimal currentTotalPriceFromDb = itemVos.stream().map(item -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(item.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            return skuInfoEntity.getPrice().multiply(new BigDecimal(item.getCount()));
        }).reduce((a, b) -> a.add(b)).get();




        if (totalPrice.compareTo(currentTotalPriceFromDb) != 0) {
            throw new OrderException("当前价格发生了变化请重新查看");
        }

        //检验库存是否充足并锁定库存
       List<SkuLockVo> lockVos=itemVos.stream().map(orderItemVo -> {
           SkuLockVo skuLockVo = new SkuLockVo();
           skuLockVo.setCount(orderItemVo.getCount());
           skuLockVo.setSkuId(orderItemVo.getSkuId());
           skuLockVo.setOrderToken(orderToken);
           return skuLockVo;
       }).collect(Collectors.toList());
        Resp<Object> checkAndLockStoreResp = wmsClient.checkAndLockStore(lockVos);
        if (checkAndLockStoreResp.getCode() != 0) {
            throw new OrderException(checkAndLockStoreResp.getMsg());
        }


        //下单
        UserInfo userInfo = AuthInterceptor.getUserInfo();
        Long userId = userInfo.getId();
        orderSubmitVo.setUserId(userId);

        try {
            Resp<OrderEntity> saveResp = omsClient.saveOrder(orderSubmitVo);
        } catch (Exception e) {
            amqpTemplate.convertAndSend(ORDER_EXCHANGE, "stock:unlock", orderToken);
            e.printStackTrace();
            throw new OrderException("服务器繁忙，下单失败，请稍后再试");

        }


        //发送消息删除购物车
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<Long> skuIds = itemVos.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());

        map.put("skuIds", skuIds);
        amqpTemplate.convertAndSend(ORDER_EXCHANGE,"cart.delete",map);

    }

}
