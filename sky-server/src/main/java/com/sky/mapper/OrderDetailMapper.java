package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 统计日期范围内销量Top10
     */
    @Select("SELECT od.name, SUM(od.number) as number FROM order_detail od " +
            "JOIN orders o ON od.order_id = o.id " +
            "WHERE o.status = 5 AND o.order_time BETWEEN #{begin} AND #{end} " +
            "GROUP BY od.name ORDER BY number DESC LIMIT 10")
    List<Map<String, Object>> getSalesTop10(java.time.LocalDateTime begin, java.time.LocalDateTime end);
}
