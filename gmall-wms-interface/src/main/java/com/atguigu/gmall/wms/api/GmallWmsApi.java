package com.atguigu.gmall.wms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wms.api.entity.WareSkuEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 31/3/2020 下午1:59
 */
public interface GmallWmsApi {
    @GetMapping("wms/waresku/{skuId}")
     Resp<List<WareSkuEntity>> listWareSkuEntity(@PathVariable Long skuId);
}
