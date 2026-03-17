package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    /**
     * 添加商品到购物车
     */
    @Override
    public void add(ShoppingCartDTO dto) {
        Long userId = BaseContext.getCurrentId();

        // 查询是否已存在
        ShoppingCart cart = ShoppingCart.builder()
                .userId(userId)
                .dishId(dto.getDishId())
                .setmealId(dto.getSetmealId())
                .dishFlavor(dto.getDishFlavor())
                .build();
        ShoppingCart existing = shoppingCartMapper.getByCondition(cart);

        if (existing != null) {
            // 已存在，数量+1
            existing.setNumber(existing.getNumber() + 1);
            shoppingCartMapper.updateNumber(existing);
            log.info("购物车商品数量+1: {}", existing);
        } else {
            // 不存在，新增记录
            ShoppingCart newCart = ShoppingCart.builder()
                    .userId(userId)
                    .dishId(dto.getDishId())
                    .setmealId(dto.getSetmealId())
                    .dishFlavor(dto.getDishFlavor())
                    .number(1)
                    .createTime(LocalDateTime.now())
                    .build();

            // 查询菜品或套餐信息
            if (dto.getDishId() != null) {
                // 菜品
                Dish dish = dishMapper.getById(dto.getDishId());
                newCart.setName(dish.getName());
                newCart.setAmount(dish.getPrice());
                newCart.setImage(dish.getImage());
            } else if (dto.getSetmealId() != null) {
                // 套餐
                Setmeal setmeal = setmealMapper.getById(dto.getSetmealId());
                newCart.setName(setmeal.getName());
                newCart.setAmount(setmeal.getPrice());
                newCart.setImage(setmeal.getImage());
            }

            shoppingCartMapper.insert(newCart);
            log.info("新增购物车商品: {}", newCart);
        }
    }

    /**
     * 查看购物车
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.getByUserId(userId);
    }

    /**
     * 删除购物车中一个商品
     */
    @Override
    public void deleteOne(ShoppingCartDTO dto) {
        Long userId = BaseContext.getCurrentId();

        // 查询
        ShoppingCart cart = ShoppingCart.builder()
                .userId(userId)
                .dishId(dto.getDishId())
                .setmealId(dto.getSetmealId())
                .dishFlavor(dto.getDishFlavor())
                .build();
        ShoppingCart existing = shoppingCartMapper.getByCondition(cart);

        if (existing == null) {
            throw new ShoppingCartBusinessException("购物车中没有该商品");
        }

        if (existing.getNumber() > 1) {
            // 数量>1，减1
            existing.setNumber(existing.getNumber() - 1);
            shoppingCartMapper.updateNumber(existing);
            log.info("购物车商品数量-1: {}", existing);
        } else {
            // 数量=1，删除
            shoppingCartMapper.deleteById(existing.getId());
            log.info("删除购物车商品: {}", existing);
        }
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
        log.info("清空购物车, userId={}", userId);
    }
}
