package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 条件查询购物车
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} " +
            "AND dish_id = #{dishId} AND setmeal_id = #{setmealId} " +
            "AND dish_flavor = #{dishFlavor}")
    ShoppingCart getByCondition(ShoppingCart cart);

    /**
     * 插入购物车记录
     */
    @Insert("INSERT INTO shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, " +
            "number, amount, image, create_time) VALUES " +
            "(#{name}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, " +
            "#{number}, #{amount}, #{image}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ShoppingCart cart);

    /**
     * 更新数量
     */
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumber(ShoppingCart cart);

    /**
     * 查询用户购物车列表
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ShoppingCart> getByUserId(Long userId);

    /**
     * 根据id删除
     */
    @Delete("DELETE FROM shopping_cart WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 清空用户购物车
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}
