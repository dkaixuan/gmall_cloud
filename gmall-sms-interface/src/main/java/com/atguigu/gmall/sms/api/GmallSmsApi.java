package com.atguigu.gmall.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午7:35
 */
public interface GmallSmsApi {
    @GetMapping("sms/skubounds/{skuId}")
     Resp<List<SaleVo>> querySkuSalesBySkuId(@PathVariable("skuId") Long skuId);


}
