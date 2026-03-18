package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单明细 Mapper
 */
@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细
     */
    @Insert("<script>" +
            "INSERT INTO order_detail (name, order_id, dish_id, setmeal_id, dish_flavor, number, amount, image) VALUES " +
            "<foreach collection=\"list\" item=\"item\" separator=\",\">" +
            "(#{item.name}, #{item.orderId}, #{item.dishId}, #{item.setmealId}, #{item.dishFlavor}, #{item.number}, #{item.amount}, #{item.image})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<OrderDetail> list);

    /**
     * 根据订单id查询明细
     */
    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
