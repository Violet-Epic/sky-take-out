package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
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
     * 查询订单历史
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     */
    OrderVO getById(Long id);

    /**
     * 取消订单
     */
    void cancel(Long id);

    /**
     * 再来一单
     */
    void repetition(Long id);
}
