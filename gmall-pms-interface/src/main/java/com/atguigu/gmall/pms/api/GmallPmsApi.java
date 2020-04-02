package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.entity.BrandEntity;
import com.atguigu.gmall.pms.api.entity.CategoryEntity;
import com.atguigu.gmall.pms.api.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.api.entity.SkuInfoEntity;
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

    @GetMapping("pms/skuinfo/{spuId}")
    Resp<List<SkuInfoEntity>> listSkuInfoBySpuId(@PathVariable Long spuId);

    @GetMapping("pms/brand/info/{brandId}")
    Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId);

    @GetMapping("pms/productattrvalue/{spuId}")
    Resp<List<ProductAttrValueEntity>> querySearchAttrValueBySpuId(@PathVariable("spuId") Long spuId);


    @GetMapping("pms/category")
     Resp<List<CategoryEntity>> listCategory(@RequestParam(value = "level",defaultValue ="0") Integer level,
                                                   @RequestParam(value = "parentCid",required = false) Integer parentCid);



}
