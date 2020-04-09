package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:17
 */
@FeignClient("gmall-ums")
public interface GmallUmsClient extends GmallUmsApi {
}
