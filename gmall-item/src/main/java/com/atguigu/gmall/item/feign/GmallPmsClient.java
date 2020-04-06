package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午7:21
 */
@FeignClient("gmall-pms")
public interface GmallPmsClient  extends GmallPmsApi {
}
