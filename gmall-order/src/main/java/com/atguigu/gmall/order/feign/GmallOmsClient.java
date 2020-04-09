package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.order.api.GmallOmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:20
 */
@FeignClient("gmall-oms")
public interface GmallOmsClient extends GmallOmsApi {
}
