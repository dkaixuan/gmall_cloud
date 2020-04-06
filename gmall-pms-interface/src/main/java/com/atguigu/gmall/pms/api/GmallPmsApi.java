package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 31/3/2020 下午1:27
 */
public interface GmallPmsApi {

    @GetMapping("pms/spuinfo/list")
    Resp<PageVo> list(QueryCondition queryCondition);


    @GetMapping("pms/spuinfo/info/{id}")
    Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);

    @GetMapping("pms/skuinfo/{spuId}")
    Resp<List<SkuInfoEntity>> listSkuInfoBySpuId(@PathVariable Long spuId);

    @GetMapping("pms/skuimages/{skuId}")
    Resp<List<SkuImagesEntity>> querySkuImagesBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skuinfo/info/{skuId}")
    Resp<SkuInfoEntity> querySkuById(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skusaleattrvalue/sku/{skuId}")
     Resp<List<SkuSaleAttrValueEntity>> querySkuSaleAttrValueBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/brand/info/{brandId}")
    Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId);

    @GetMapping("pms/productattrvalue/{spuId}")
    Resp<List<ProductAttrValueEntity>> querySearchAttrValueBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/category")
    Resp<List<CategoryEntity>> listCategory(@RequestParam(value = "level", defaultValue = "0") Integer level,
                                            @RequestParam(value = "parentCid", required = false) Integer parentCid);


    @GetMapping("pms/skusaleattrvalue/{spuId}")
    Resp<List<SkuSaleAttrValueEntity>> querySkuSaleAttrValueBySpuId(@PathVariable("spuId") Long spuId);


    @GetMapping("pms/attrgroup/item/group/{cid}/{spuId}")
     Resp<List<ItemGroupVo>> queryItemGroupVoByCidAndSpuId(@PathVariable("cid") Long cid,
                                                                 @PathVariable("spuId") Long spuId);

    @GetMapping("pms/spuinfodesc/info/{spuId}")
     Resp<SpuInfoDescEntity> querySpuDescBySpuId(@PathVariable("spuId") Long spuId);
}
