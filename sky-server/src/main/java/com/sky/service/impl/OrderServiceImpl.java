package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final AddressBookMapper addressBookMapper;

    /**
     * 提交订单
     */
    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        Long userId = BaseContext.getCurrentId();
        
        // 1. 查询购物车数据
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByUserId(userId);
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new OrderBusinessException("购物车为空");
        }
        
        // 2. 查询地址
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException("地址不存在");
        }
        
        // 3. 生成订单号
        String orderNumber = UUID.randomUUID().toString().replace("-", "");
        
        // 4. 构建订单
        Orders orders = Orders.builder()
                .number(orderNumber)
                .status(Orders.PENDING_PAYMENT)
                .userId(userId)
                .addressBookId(dto.getAddressBookId())
                .orderTime(LocalDateTime.now())
                .payMethod(dto.getPayMethod())
                .payStatus(Orders.UN_PAID)
                .amount(dto.getAmount())
                .remark(dto.getRemark())
                .userName(addressBook.getConsignee())
                .phone(addressBook.getPhone())
                .address(addressBook.getDetail())
                .consignee(addressBook.getConsignee())
                .estimatedDeliveryTime(dto.getEstimatedDeliveryTime())
                .deliveryStatus(dto.getDeliveryStatus())
                .packAmount(dto.getPackAmount())
                .tablewareNumber(dto.getTablewareNumber())
                .tablewareStatus(dto.getTablewareStatus())
                .build();
        
        // 5. 插入订单
        orderMapper.insert(orders);
        
        // 6. 构建订单明细
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail detail = OrderDetail.builder()
                    .name(cart.getName())
                    .orderId(orders.getId())
                    .dishId(cart.getDishId())
                    .setmealId(cart.getSetmealId())
                    .dishFlavor(cart.getDishFlavor())
                    .number(cart.getNumber())
                    .amount(cart.getAmount())
                    .image(cart.getImage())
                    .build();
            orderDetails.add(detail);
        }
        
        // 7. 批量插入订单明细
        orderDetailMapper.insertBatch(orderDetails);
        
        // 8. 清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        
        log.info("订单提交成功: orderNumber={}, amount={}", orderNumber, orders.getAmount());
        
        // 9. 返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orderNumber)
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 支付成功（模拟）
     */
    @Transactional
    @Override
    public void paymentSuccess(OrdersPaymentDTO dto) {
        // 根据订单号查询订单
        Orders orders = orderMapper.getByNumber(dto.getOrderNumber());
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }
        
        // 更新订单状态
        orders.setPayStatus(Orders.PAID);
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setCheckoutTime(LocalDateTime.now());
        orderMapper.update(orders);
        
        log.info("支付成功: orderNumber={}", dto.getOrderNumber());
    }

    /**
     * 查询订单历史
     */
    @Override
    public PageResult page(int page, int pageSize, Integer status) {
        // TODO: 分页查询实现
        return null;
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderVO getById(Long id) {
        // TODO: 实现
        return null;
    }

    /**
     * 取消订单
     */
    @Override
    public void cancel(Long id) {
        // TODO: 实现
    }

    /**
     * 再来一单
     */
    @Override
    public void repetition(Long id) {
        // TODO: 实现
    }
}
