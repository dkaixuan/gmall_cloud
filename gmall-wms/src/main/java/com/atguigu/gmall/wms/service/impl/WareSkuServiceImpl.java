package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.wms.dao.WareSkuDao;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    private static final String KEY_PREFIX = "store:lock:";
    private static final String ORDER_EXCHANGE="GMALL-ORDER-EXCHANGE";
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public String checkAndLockStore(List<SkuLockVo> skuLockVos) {
        //检验并锁定库存
        skuLockVos.forEach(skuLockVo -> {
            lockStore(skuLockVo);
        });


        List<SkuLockVo> unLockList = skuLockVos.stream().filter(skuLockVo -> skuLockVo.getLock() == false).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(unLockList)) {
            //解锁已锁定商品的库存
            List<SkuLockVo> lockSku = skuLockVos.stream().filter(SkuLockVo::getLock).collect(Collectors.toList());
            lockSku.forEach(skuLockVo -> {
                wareSkuDao.unLockStore(skuLockVo.getWareSkuId(), skuLockVo.getCount());
            });
            //提示锁定失败的商品
            Object[] skuIds = unLockList.stream().map(skuLockVo -> skuLockVo.getSkuId()).toArray();
            return "抱歉，您本单购买的以下商品暂时无货"+skuIds;
        }


        String orderToken = skuLockVos.get(0).getOrderToken();
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + orderToken, JSON.toJSONString(skuLockVos));

        //锁定成功，发送延时消息，定时解锁
        amqpTemplate.convertAndSend(ORDER_EXCHANGE, "stock.ttl", orderToken);
        return null;
    }


    private void lockStore(SkuLockVo skuLockVo) {
        //查询剩余库存，并锁定，为保证原子性，加锁
        RLock lock = redissonClient.getLock("stock:" + skuLockVo.getSkuId());
        lock.lock();
        try {
            List<WareSkuEntity> wareSkuEntities = wareSkuDao.checkStore(skuLockVo.getSkuId(), skuLockVo.getCount());
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                Long id = wareSkuEntities.get(0).getId();
                wareSkuDao.lockStore(id, skuLockVo.getCount());
                skuLockVo.setWareSkuId(id);
                skuLockVo.setLock(true);
            } else {
                skuLockVo.setLock(false);
            }
        } finally {
            lock.unlock();
        }


    }

}