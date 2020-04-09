package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 31/3/2020 下午3:24
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {

}
