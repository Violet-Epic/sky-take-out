package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.*;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     */
    @Insert("INSERT INTO orders (number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, " +
            "user_name, phone, address, consignee, estimated_delivery_time, delivery_status, " +
            "pack_amount, tableware_number, tableware_status) " +
            "VALUES (#{number}, #{status}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, " +
            "#{payMethod}, #{payStatus}, #{amount}, #{remark}, #{userName}, #{phone}, #{address}, " +
            "#{consignee}, #{estimatedDeliveryTime}, #{deliveryStatus}, #{packAmount}, #{tablewareNumber}, #{tablewareStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Orders orders);

    /**
     * 根据id查询订单
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders getById(Long id);

    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM orders WHERE number = #{number}")
    Orders getByNumber(String number);

    /**
     * 更新订单
     */
    @Update("UPDATE orders SET status = #{status}, pay_status = #{payStatus}, checkout_time = #{checkoutTime}, " +
            "cancel_reason = #{cancelReason}, rejection_reason = #{rejectionReason}, " +
            "cancel_time = #{cancelTime}, delivery_time = #{deliveryTime} WHERE id = #{id}")
    void update(Orders orders);

    /**
     * 分页查询订单
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO dto);

    /**
     * 根据状态统计订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 统计某个日期范围内的营业额（已完成订单）
     */
    @Select("SELECT SUM(amount) FROM orders WHERE status = 5 AND order_time BETWEEN #{begin} AND #{end}")
    Double sumAmountByDate(java.time.LocalDateTime begin, java.time.LocalDateTime end);

    /**
     * 统计某个日期范围内的订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_time BETWEEN #{begin} AND #{end}")
    Integer countOrderByDate(java.time.LocalDateTime begin, java.time.LocalDateTime end);

    /**
     * 统计某个日期范围内的有效订单数（已完成）
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = 5 AND order_time BETWEEN #{begin} AND #{end}")
    Integer countValidOrderByDate(java.time.LocalDateTime begin, java.time.LocalDateTime end);
}
