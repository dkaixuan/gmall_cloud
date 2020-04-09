package com.atguigu.gmall.wms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 31/3/2020 下午1:59
 */
public interface GmallWmsApi {
    @GetMapping("wms/waresku/{skuId}")
     Resp<List<WareSkuEntity>> listWareSkuEntity(@PathVariable Long skuId);


    @PostMapping("wms/waresku")
     Resp<Object> checkAndLockStore(@RequestBody List<SkuLockVo> skuLockVos);

}
