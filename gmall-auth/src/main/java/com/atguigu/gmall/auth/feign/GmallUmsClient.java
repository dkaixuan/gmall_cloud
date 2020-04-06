package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 4/4/2020 上午11:03
 */
@FeignClient("gmall-ums")
public interface GmallUmsClient extends GmallUmsApi {
}
