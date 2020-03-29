package com.atguigu.gmall.pms.clients;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.SkuSaleVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author kaixuan
 * @version 1.0
 * @date 28/3/2020 下午8:01
 */
@FeignClient(value = "gmall-sms")
public interface SmsClient {

    @PostMapping("sms/skubounds/sku/sale/save")
    public Resp<Object> saveSale(@RequestBody SkuSaleVo skuSaleVo);


}
