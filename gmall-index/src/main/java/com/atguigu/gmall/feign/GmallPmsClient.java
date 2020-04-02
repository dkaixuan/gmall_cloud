package com.atguigu.gmall.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 1/4/2020 下午1:33
 */
@FeignClient("gmall-pms")
public interface GmallPmsClient extends GmallPmsApi {

}
