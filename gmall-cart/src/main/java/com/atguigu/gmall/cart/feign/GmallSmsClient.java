package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午7:21
 */
@FeignClient("gmall-sms")
public interface GmallSmsClient extends GmallSmsApi {
}
