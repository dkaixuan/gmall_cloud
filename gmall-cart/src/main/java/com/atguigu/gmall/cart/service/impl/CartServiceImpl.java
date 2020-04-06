package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptor.AuthInterceptor;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.pojo.UserInfo;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.SaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kaixuan
 * @version 1.0
 * @date 5/4/2020 下午1:52
 */
@Service
public class CartServiceImpl implements CartService {

    private static final String KEY_PREFIX = "user:cart:";
    private static final String PRICE_PREFIX = "cart:sku:";


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;

    @Override
    public void addCart(Cart cart) {
        String key = getLoginState();
        //获取的是用户hash操作对象
        BoundHashOperations<String, Object, Object> userHashOperations = stringRedisTemplate.boundHashOps(key);

        String skuId = cart.getSkuId().toString();

        //判断购物车中是否有该记录
        if (userHashOperations.hasKey(skuId.toString())) {
            String cartJson = userHashOperations.get(skuId).toString();
            Cart cartFromCache = JSON.parseObject(cartJson, Cart.class);
            cartFromCache.setCount(cartFromCache.getCount() + cart.getCount());
            userHashOperations.put(skuId, JSON.toJSONString(cartFromCache));
        } else {
            cart.setCheck(true);
            //查询sku相关信息

            Resp<SkuInfoEntity> skuInfoEntityResp = pmsClient.querySkuById(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            if (skuInfoEntity != null) {
                cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                cart.setTitle(skuInfoEntity.getSkuTitle());
                cart.setPrice(new BigDecimal(skuInfoEntity.getPrice().toString()));
                //查询销售属性相关信息
                Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = pmsClient.querySkuSaleAttrValueBySkuId(cart.getSkuId());
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrValueResp.getData();
                cart.setSaleAttrValues(skuSaleAttrValueEntities);
                //查询营销信息
                Resp<List<SaleVo>> saleResp = smsClient.querySkuSalesBySkuId(cart.getSkuId());
                List<SaleVo> saleVos = saleResp.getData();
                cart.setSales(saleVos);
                //查询库存信息
                Resp<List<WareSkuEntity>> wareSkuResp = wmsClient.listWareSkuEntity(cart.getSkuId());
                List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
                if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                    cart.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
                }
                stringRedisTemplate.opsForValue().set(PRICE_PREFIX +skuId,skuInfoEntity.getPrice().toString());
                userHashOperations.put(skuId, JSON.toJSONString(cart));
            }
        }
    }

    private String getLoginState() {
        String key = KEY_PREFIX;
        UserInfo userInfo = AuthInterceptor.getUserInfo();
        Long id = userInfo.getId();
        String userKey = userInfo.getUserKey();
        if (id != null) {
            key += id;
        } else {
            key += userKey;
        }
        return key;
    }


    @Override
    public List<Cart> queryCarts() {
        UserInfo userInfo = AuthInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        Long id = userInfo.getId();
        List<Cart> unLoginCartList = null;

        //查询没登录的购物车
        BoundHashOperations<String, Object, Object> unLoginHashOperations = stringRedisTemplate.boundHashOps(KEY_PREFIX + userKey);
        List<Object> cartJsonList = unLoginHashOperations.values();
        if (!CollectionUtils.isEmpty(cartJsonList)) {
            unLoginCartList = cartJsonList.stream().map(cartJson ->{
                Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
                //设置当前价格
                String currentPrice = stringRedisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId());
                cart.setCurrentPrice(new BigDecimal(currentPrice));
                return cart;
            }).collect(Collectors.toList());
        }
        //如果没登陆直接返回
        if (id == null) {
            return unLoginCartList;
        }
        //用户在登录完成时需要合并未登录状态的购物车
        BoundHashOperations<String, Object, Object> loginHashOperations = stringRedisTemplate.boundHashOps(KEY_PREFIX + id);
        //查询登录状态的购物车
        if (!CollectionUtils.isEmpty(unLoginCartList)) {
            unLoginCartList.forEach(cart -> {
                Integer count = cart.getCount();
                if (loginHashOperations.hasKey(cart.getSkuId().toString())){
                    String cartJson = loginHashOperations.get(cart.getSkuId().toString()).toString();
                    cart = JSON.parseObject(cartJson, Cart.class);
                    cart.setCount(cart.getCount() + count);
                }
                loginHashOperations.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
            });

        }

        //同步完成需要删除未登录状态的购物车
        stringRedisTemplate.delete(KEY_PREFIX+userKey);
        List<Object> loginCartJsonList = loginHashOperations.values();
        List<Cart> cartList = loginCartJsonList.stream().map(cartJson -> {
            Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
            String currentPrice = stringRedisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId().toString());
            cart.setCurrentPrice(new BigDecimal(currentPrice));
            return cart;
        }).collect(Collectors.toList());
        return cartList;
    }


    @Override
    public void updateCart(Cart cart) {
        String key = getLoginState();
        String skuId=cart.getSkuId().toString();
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(key);

        if (hashOperations.hasKey(cart.getSkuId().toString())) {
            String cartJson = hashOperations.get(skuId).toString();
            Cart cartFromDb = JSON.parseObject(cartJson, Cart.class);
            cartFromDb.setCount(cart.getCount());
            hashOperations.put(skuId, JSON.toJSONString(cartFromDb));
        }

    }

    @Override
    public void deleteCartBySkuId(Long skuId) {
        String key = getLoginState();
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(key);
        if (hashOperations.hasKey(skuId.toString())) {
            hashOperations.delete(skuId.toString());

        }
    }


}
