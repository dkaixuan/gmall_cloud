package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 8/4/2020 下午8:21
 */

@FeignClient("gmall-ums")
public interface GmallUmsClient extends GmallUmsApi {
}
