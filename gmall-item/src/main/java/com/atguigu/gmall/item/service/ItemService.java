package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.sms.vo.SaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午4:44
 */
@Service
public class ItemService {

    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public ItemVo queryItemVo(Long skuId) {
        ItemVo itemVo = new ItemVo();
        itemVo.setSkuId(skuId);
        CompletableFuture<Object> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> resp = pmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = resp.getData();
            if (skuInfoEntity == null) {
                return itemVo;
            }
            //根据id查询sku
            itemVo.setSkuTitle(skuInfoEntity.getSkuTitle());
            itemVo.setSubTitle(skuInfoEntity.getSkuSubtitle());
            itemVo.setPrice(skuInfoEntity.getPrice());
            itemVo.setWeight(skuInfoEntity.getWeight());
            return skuInfoEntity;
        }, threadPoolExecutor);

        CompletableFuture<Void> spuInfoFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //根据sku中的spuId查询spu
            SkuInfoEntity skuInfoEntity1 = (SkuInfoEntity) skuInfoEntity;
            Resp<SpuInfoEntity> spuResp = pmsClient.querySpuById(skuInfoEntity1.getSpuId());
            SpuInfoEntity SpuInfoEntity = spuResp.getData();
            itemVo.setSpuId(skuInfoEntity1.getSpuId());
            itemVo.setSpuName(SpuInfoEntity.getSpuName());
        }, threadPoolExecutor);


        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            //根据skuId查询图片列表
            Resp<List<SkuImagesEntity>> skuImagesResp = pmsClient.querySkuImagesBySkuId(skuId);
            List<SkuImagesEntity> skuImagesEntities = skuImagesResp.getData();
            itemVo.setPics(skuImagesEntities);
        }, threadPoolExecutor);


        CompletableFuture<Void> brandFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //根据sku中的brandId和Category查询品牌和分类
            SkuInfoEntity skuInfoEntity1 = (SkuInfoEntity) skuInfoEntity;
            Resp<BrandEntity> brandEntityResp = pmsClient.queryBrandById(skuInfoEntity1.getBrandId());
            BrandEntity brandEntity = brandEntityResp.getData();
            itemVo.setBrandEntity(brandEntity);
            Resp<CategoryEntity> categoryEntityResp = pmsClient.queryCategoryById(skuInfoEntity1.getCatalogId());
            CategoryEntity categoryEntity = categoryEntityResp.getData();
            itemVo.setCategoryEntity(categoryEntity);
        }, threadPoolExecutor);


        CompletableFuture<Void> saleFuture =  CompletableFuture.runAsync(() -> {
            //根据skuId查询营销信息
            itemVo.setSales(null);
            Resp<List<SaleVo>> salesResp = smsClient.querySkuSalesBySkuId(skuId);
            List<SaleVo> saleVoList = salesResp.getData();
            itemVo.setSales(saleVoList);
        },threadPoolExecutor);


        CompletableFuture<Void> wareFuture = CompletableFuture.runAsync(() -> {
            //根据skuId查询库存信息
            Resp<List<WareSkuEntity>> wareResp = wmsClient.listWareSkuEntity(skuId);
            List<WareSkuEntity> wareSkuEntities = wareResp.getData();
            itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
        }, threadPoolExecutor);


        CompletableFuture<Void> skuSaleFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //根据spuId查询所有skuIds，再去查询所有的销售属性
            Resp<List<SkuSaleAttrValueEntity>> saleAttrValueByResp = pmsClient.querySkuSaleAttrValueBySpuId(((SkuInfoEntity) skuInfoEntity).getSpuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrValueByResp.getData();
            itemVo.setSaleAttrs(skuSaleAttrValueEntities);
        }, threadPoolExecutor);


        CompletableFuture<Void> spuDescFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //海报信息
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = pmsClient.querySpuDescBySpuId(((SkuInfoEntity) skuInfoEntity).getSpuId());
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
            if (spuInfoDescEntity != null) {
                String decript = spuInfoDescEntity.getDecript();
                String[] split = StringUtils.split(decript, ",");
                itemVo.setImages(Arrays.asList(split));
            }
        }, threadPoolExecutor);


        CompletableFuture<Void> groupVoFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            SkuInfoEntity skuInfoEntity1 = (SkuInfoEntity) skuInfoEntity;
            //根据spuId和cateId查询组及组下规格参数
            Resp<List<ItemGroupVo>> gruopListResp = pmsClient.queryItemGroupVoByCidAndSpuId(skuInfoEntity1.getCatalogId(), skuInfoEntity1.getSpuId());
            List<ItemGroupVo> itemGroupVos = gruopListResp.getData();
            itemVo.setGroups(itemGroupVos);
        }, threadPoolExecutor);


        CompletableFuture.allOf(spuInfoFuture, brandFuture, skuImagesFuture, saleFuture,wareFuture,
                skuSaleFuture,spuDescFuture,groupVoFuture).join();


        return itemVo;
    }



}
