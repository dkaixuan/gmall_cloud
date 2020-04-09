package com.atguigu.gmall.service;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.annoatation.GmallCache;
import com.atguigu.gmall.order.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 1/4/2020 下午1:29
 */
@Service
public class IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;


    public List<CategoryEntity> queryLevel1Categories() {
        Resp<List<CategoryEntity>> listResp = gmallPmsClient.listCategory(1, null);
        return listResp.getData();
    }



    @GmallCache(prefix ="sku:info:",timeout =5,random =10)
    public List<SkuInfoEntity> listSkuInfoBySpuId(Long spuId) {
        Resp<List<SkuInfoEntity>> listResp = gmallPmsClient.listSkuInfoBySpuId(spuId);
        return listResp.getData();
    }







}
