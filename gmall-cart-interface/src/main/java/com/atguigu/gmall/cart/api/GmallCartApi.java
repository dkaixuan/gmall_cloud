package com.atguigu.gmall.cart.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.pojo.Cart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 6/4/2020 下午4:18
 */
public interface GmallCartApi {

    @GetMapping("cart")
    Resp<List<Cart>> queryCarts();

    @GetMapping("cart/userCarts/{userId}")
    Resp<List<Cart>> queryCartsByUserId(@PathVariable("userId") Long userId) ;
}
