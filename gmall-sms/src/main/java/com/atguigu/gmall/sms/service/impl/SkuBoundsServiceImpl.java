package com.atguigu.gmall.sms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.vo.SaleVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuLadderDao skuLadderDao;
    @Autowired
    private SkuFullReductionDao skuFullReductionDao;
    @Autowired
    private SkuFullReductionDao reductionDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }


    @Override
    public void saveSaLe(SkuSaleVo skuSaleVo) {
        //3.保存营销信息的三张表
        //3.1保存sms_sku_bounds

        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleVo, skuBoundsEntity, new String[]{"work"});
        List<Integer> work = skuSaleVo.getWork();
        skuBoundsEntity.setWork(work.get(3) * 1 + work.get(1) + work.get(1) * 4 + work.get(0) * 8);
        this.save(skuBoundsEntity);
        //3.2保存sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleVo, skuLadderEntity, new String[]{"work"});
        skuLadderDao.insert(skuLadderEntity);
        //3.3保存sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleVo, skuFullReductionEntity, new String[]{"work"});
        skuFullReductionDao.insert(skuFullReductionEntity);
    }

    @Override
    public List<SaleVo> querSaleBySkuId(Long skuId) {
        List<SaleVo> saleVos = new ArrayList<>();
        //查询积分信息
        SkuBoundsEntity skuBoundsEntity = this.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            SaleVo boundsVo = new SaleVo();
            boundsVo.setType("积分/");
            if (skuBoundsEntity.getGrowBounds() != null && skuBoundsEntity.getGrowBounds().intValue() > 0) {
                boundsVo.setDesc("成长积分送" + skuBoundsEntity.getGrowBounds() + "，赠送积分");
            }

            saleVos.add(boundsVo);
        }
        //查询打折
        SkuLadderEntity skuLadderEntity = skuLadderDao.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        SaleVo ladderVo = new SaleVo();
        if (skuLadderEntity != null) {
            ladderVo.setType("打折");
            ladderVo.setDesc("满" + skuLadderEntity.getFullCount() + "件,打" + skuLadderEntity.getDiscount().divide(new BigDecimal(10 ))+ "折");
        }

        //查询满减
        SkuFullReductionEntity reductionEntity = reductionDao.selectOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (reductionEntity != null) {
            SaleVo reductionVo = new SaleVo();
            reductionVo.setType("满减");
            reductionVo.setDesc("满" + reductionEntity.getFullPrice() + "减" + reductionEntity.getReducePrice());
            saleVos.add(reductionVo);
        }


        return saleVos;
    }

}