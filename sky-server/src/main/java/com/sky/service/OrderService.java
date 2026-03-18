package com.sky.service;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 提交订单
     */
    OrderSubmitVO submit(OrdersSubmitDTO dto);

    /**
     * 支付成功（模拟）
     */
    void paymentSuccess(OrdersPaymentDTO dto);

    /**
     * 用户端 - 查询订单历史
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     */
    OrderVO getById(Long id);

    /**
     * 用户端 - 取消订单
     */
    void cancel(Long id);

    /**
     * 再来一单
     */
    void repetition(Long id);

    /**
     * 管理端 - 条件查询订单
     */
    PageResult conditionSearch(OrdersPageQueryDTO dto);

    /**
     * 各个状态的订单数量统计
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     */
    void confirm(OrdersConfirmDTO dto);

    /**
     * 拒单
     */
    void rejection(OrdersRejectionDTO dto);

    /**
     * 管理端 - 取消订单
     */
    void adminCancel(OrdersCancelDTO dto);

    /**
     * 派送订单
     */
    void delivery(Long id);

    /**
     * 完成订单
     */
    void complete(Long id);
}
