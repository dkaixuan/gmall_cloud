package com.atguigu.gmall.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.api.entity.CategoryEntity;
import com.atguigu.gmall.pms.api.entity.SkuInfoEntity;
import com.atguigu.gmall.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 1/4/2020 下午1:26
 */
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;


    @GetMapping("/cates")
    public Resp<List<CategoryEntity>> queryLevel1Categories() {
        List<CategoryEntity> categoryEntityList = indexService.queryLevel1Categories();
        return Resp.ok(categoryEntityList);
    }

    @GetMapping("{spuId}")
    public Resp<List<SkuInfoEntity>> listSkuInfoBySpuId(@PathVariable Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = indexService.listSkuInfoBySpuId(spuId);
        return Resp.ok(skuInfoEntities);
    }


}
