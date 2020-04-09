package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午7:21
 */
@FeignClient("gmall-wms")
public interface GmallWmsClient extends GmallWmsApi {
}
