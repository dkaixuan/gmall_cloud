package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 5/4/2020 下午1:29
 */
@RestController
@RequestMapping("cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @PostMapping
    public Resp addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return Resp.ok(null);
    }


    @PostMapping("update")
    public Resp<Object> updateCart(@RequestBody Cart cart) {

        cartService.updateCart(cart);

        return Resp.ok(null);
    }

    @DeleteMapping("{skuId}")
    public Resp deleteCartBySkuId(@PathVariable("skuId") Long skuId) {
        cartService.deleteCartBySkuId(skuId);

        return Resp.ok(null);
    }


    @GetMapping
    public Resp<List<Cart>> queryCarts() {
        List<Cart> carts = this.cartService.queryCarts();
        return Resp.ok(carts);
    }


}
