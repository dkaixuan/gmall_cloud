package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.pojo.Cart;

import java.util.List;

/**
 * @author kaixuan
 * @version 1.0
 * @date 5/4/2020 下午1:51
 */
public interface CartService {
    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateCart(Cart cart);

    void deleteCartBySkuId(Long skuId);
}
