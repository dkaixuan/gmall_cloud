package com.atguigu.gmall.pms.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.atguigu.gmall.pms.clents.SmsClient;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.BaseAttrVo;
import com.atguigu.gmall.pms.vo.SkuInfoVo;
import com.atguigu.gmall.pms.vo.SkuSaleVo;
import com.atguigu.gmall.pms.vo.SpuInfoVo;
import com.sun.xml.bind.v2.TODO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private ProductAttrValueDao attrValueDao;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SmsClient smsClient;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo listSpuByCatalogId(QueryCondition queryCondition, Long catId) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (catId != 0&&catId!=null) {
            wrapper.eq("catalog_id", catId);
        }
        String key = queryCondition.getKey();

        if (!StringUtils.isEmpty(key)) {
            wrapper.and(t->t.like("spu_name",key).or()
                    .like("spu_description",key));
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),
                wrapper
        );
        return new PageVo(page);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfoAndImagesAndSkuInfoAndAttrValueAndSoOn(SpuInfoVo spuInfoVo) {
        //1保存spu相关的三张表
        //1.1保存pms_spu_info信息

        this.save(spuInfoVo);
        Long spuId = spuInfoVo.getId();
        SavePmsSpuInfoDesc(spuInfoVo, spuId);
        savePmsSpuProductAttrValue(spuInfoVo);
        //------------------------------------
        //保存sku相关的三张表
        List<SkuInfoVo> skus = spuInfoVo.getSkus();
        if (CollectionUtils.isEmpty(skus)) {
            return;
        }

        skus.forEach(skuInfoVo -> {
            List<String> images = SavePmsSkuInfo(spuInfoVo, spuId, skuInfoVo);
            Long skuId = skuInfoVo.getSkuId();
            savePmsSkuImages(skuInfoVo, images, skuId);
            saveSaleAttrValue(skuInfoVo, skuId);
            //3.微服务远程调用保存营销信息的三张表
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuInfoVo, skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            smsClient.saveSale(skuSaleVo);
        });




    }

    /**
     * 2.1 保存pms_sku_info
     * @param spuInfoVo
     * @param spuId
     * @param skuInfoVo
     * @return
     */
    private List<String> SavePmsSkuInfo(SpuInfoVo spuInfoVo, Long spuId, SkuInfoVo skuInfoVo) {
        skuInfoVo.setSkuId(spuId);
        skuInfoVo.setSkuCode(RandomUtil.randomNumbers(16));
        skuInfoVo.setCatalogId(spuInfoVo.getCatalogId());
        skuInfoVo.setBrandId(spuInfoVo.getBrandId());
        List<String> images = skuInfoVo.getImages();
        if (!CollectionUtils.isEmpty(images)) {
            skuInfoVo.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoVo.getSkuDefaultImg()) ? skuInfoVo.getSkuDefaultImg() : images.get(0));
        }
        skuInfoDao.insert(skuInfoVo);
        return images;
    }

    /**
     * 2.2 保存pms_sku_images
     * @param skuInfoVo
     * @param images
     * @param skuId
     */
    private void savePmsSkuImages(SkuInfoVo skuInfoVo, List<String> images, Long skuId) {
        if (!CollectionUtils.isEmpty(images)) {
            List<SkuImagesEntity> skuImagesEntities=images.stream().map(image -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setImgUrl(image);
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setDefaultImg(StringUtils.equals(skuInfoVo.getSkuDefaultImg(), image)? 1:0);
                return skuImagesEntity;
            }).collect(Collectors.toList());
            skuImagesService.saveBatch(skuImagesEntities);
        }
    }


    /**
     * 1.2保存pms_spu_info_desc信息
     * @param spuInfoVo
     * @param spuId
     */
    private void SavePmsSpuInfoDesc(SpuInfoVo spuInfoVo, Long spuId) {
        List<String> spuImages = spuInfoVo.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuImages, ","));
            spuInfoDescDao.insert(spuInfoDescEntity);
        }
    }


    /**
     * 1.3保存pms_spu_product_attr_value
     * @param spuInfoVo
     */
    private void savePmsSpuProductAttrValue(SpuInfoVo spuInfoVo) {
        List<BaseAttrVo> baseAttrs = spuInfoVo.getBaseAttrs();
        if (CollectionUtils.isEmpty(baseAttrs)) {
            baseAttrs.forEach(baseAttr->{
                baseAttr.setSpuId(spuInfoVo.getId());
                attrValueDao.insert(baseAttr);
            });
        }
    }

    /**
     * 2.3 保存pms_sale_attr_value销售属性
     * @param skuInfoVo
     * @param skuId
     */
    private void saveSaleAttrValue(SkuInfoVo skuInfoVo, Long skuId) {
        List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVo.getSaleAttrs();
        if (!CollectionUtils.isEmpty(saleAttrs)) {
            saleAttrs.forEach(skuSaleAttrValueEntity -> skuSaleAttrValueEntity.setSkuId(skuId));
            skuSaleAttrValueService.saveBatch(saleAttrs);
        }
    }



}