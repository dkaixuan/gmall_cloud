package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kaixuan
 * @version 1.0
 * @date 2/4/2020 下午7:21
 */
@FeignClient("gmall-cart")
public interface GmallCartClient extends GmallCartApi {
}
