package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface ShoppingCartService {

    /**
     * 添加商品到购物车
     */
    void add(ShoppingCartDTO dto);

    /**
     * 查看购物车
     */
    List<ShoppingCart> list();

    /**
     * 删除购物车中一个商品
     */
    void deleteOne(ShoppingCartDTO dto);

    /**
     * 清空购物车
     */
    void clean();
}
