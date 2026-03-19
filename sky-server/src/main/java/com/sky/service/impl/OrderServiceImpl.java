package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
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
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // 通过 WebSocket 推送消息给管理端
        Map<String, Object> message = new HashMap<>();
        message.put("type", 1);  // 1 表示来单提醒
        message.put("orderId", orders.getId());
        message.put("content", "订单号：" + orders.getNumber());

        // 广播给所有在线客户端（管理端会处理 type=1 的消息）
        for (WebSocketServer server : WebSocketServer.getAllInstances()) {
            server.sendToAll(toJsonString(message));
        }

        log.info("支付成功: orderNumber={}", dto.getOrderNumber());
    }

    /**
     * 对象转 JSON 字符串
     */
    private String toJsonString(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 转换失败", e);
            return "{}";
        }
    }

    /**
     * 查询订单历史
     */
    @Override
    public PageResult page(int page, int pageSize, Integer status) {
        // 1. 设置分页参数
        PageHelper.startPage(page, pageSize);

        // 2. 构建查询条件
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();
        dto.setUserId(BaseContext.getCurrentId());
        dto.setStatus(status);

        // 3. 分页查询
        Page<Orders> pageResult = orderMapper.pageQuery(dto);

        // 4. 封装成 OrderVO
        List<OrderVO> list = new ArrayList<>();
        for (Orders orders : pageResult.getResult()) {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(orders, vo);

            // 查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
            vo.setOrderDetailList(orderDetails);

            list.add(vo);
        }

        return new PageResult(pageResult.getTotal(), list);
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderVO getById(Long id) {
        // 1. 查询订单
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 2. 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        // 3. 封装成 OrderVO
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(orders, vo);
        vo.setOrderDetailList(orderDetails);

        return vo;
    }

    /**
     * 取消订单
     */
    @Override
    public void cancel(Long id) {
        // 1. 查询订单
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException("订单不存在");
        }

        // 2. 校验订单状态（只有待付款和待接单状态可以取消）
        if (orders.getStatus() > Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException("订单状态不允许取消");
        }

        // 3. 更新订单状态
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

        log.info("订单取消成功: orderId={}", id);
    }

    /**
     * 再来一单
     */
    @Override
    public void repetition(Long id) {
        // 1. 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        if (orderDetails == null || orderDetails.isEmpty()) {
            throw new OrderBusinessException("订单明细为空");
        }

        Long userId = BaseContext.getCurrentId();

        // 2. 将订单明细添加到购物车
        for (OrderDetail detail : orderDetails) {
            ShoppingCart cart = ShoppingCart.builder()
                    .userId(userId)
                    .dishId(detail.getDishId())
                    .setmealId(detail.getSetmealId())
                    .dishFlavor(detail.getDishFlavor())
                    .number(detail.getNumber())
                    .amount(detail.getAmount())
                    .name(detail.getName())
                    .image(detail.getImage())
                    .build();
            shoppingCartMapper.insert(cart);
        }

        log.info("再来一单成功: orderId={}, userId={}", id, userId);
    }

    // ==================== 管理端方法 ====================

    /**
     * 管理端 - 条件查询订单
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO dto) {
        // 1. 设置分页参数
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 2. 分页查询
        Page<Orders> pageResult = orderMapper.pageQuery(dto);

        // 3. 封装成 OrderVO
        List<OrderVO> list = new ArrayList<>();
        for (Orders orders : pageResult.getResult()) {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(orders, vo);

            // 查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
            vo.setOrderDetailList(orderDetails);

            // 拼接订单菜品信息（用于展示）
            String orderDishes = getOrderDishes(orderDetails);
            vo.setOrderDishes(orderDishes);

            list.add(vo);
        }

        return new PageResult(pageResult.getTotal(), list);
    }

    /**
     * 各个状态的订单数量统计
     */
    @Override
    public OrderStatisticsVO statistics() {
        // 待接单数量（status = 2）
        Integer toBeConfirmed = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);
        // 待派送数量（status = 3）
        Integer confirmed = orderMapper.countByStatus(Orders.CONFIRMED);
        // 派送中数量（status = 4）
        Integer deliveryInProgress = orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS);

        return OrderStatisticsVO.builder()
                .toBeConfirmed(toBeConfirmed)
                .confirmed(confirmed)
                .deliveryInProgress(deliveryInProgress)
                .build();
    }

    /**
     * 接单
     */
    @Override
    public void confirm(OrdersConfirmDTO dto) {
        Orders orders = Orders.builder()
                .id(dto.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
        log.info("接单成功: orderId={}", dto.getId());
    }

    /**
     * 拒单
     */
    @Override
    public void rejection(OrdersRejectionDTO dto) {
        Orders orders = Orders.builder()
                .id(dto.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(dto.getRejectionReason())
                .build();
        orderMapper.update(orders);
        log.info("拒单成功: orderId={}, reason={}", dto.getId(), dto.getRejectionReason());
    }

    /**
     * 管理端 - 取消订单
     */
    @Override
    public void adminCancel(OrdersCancelDTO dto) {
        Orders orders = Orders.builder()
                .id(dto.getId())
                .status(Orders.CANCELLED)
                .cancelReason(dto.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
        log.info("取消订单成功: orderId={}, reason={}", dto.getId(), dto.getCancelReason());
    }

    /**
     * 派送订单
     */
    @Override
    public void delivery(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(orders);
        log.info("派送订单成功: orderId={}", id);
    }

    /**
     * 完成订单
     */
    @Override
    public void complete(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
        log.info("完成订单成功: orderId={}", id);
    }

    // ==================== 私有方法 ====================

    /**
     * 拼接订单菜品信息
     */
    private String getOrderDishes(List<OrderDetail> orderDetails) {
        StringBuilder sb = new StringBuilder();
        for (OrderDetail detail : orderDetails) {
            sb.append(detail.getName());
            if (detail.getDishFlavor() != null) {
                sb.append("(").append(detail.getDishFlavor()).append(")");
            }
            sb.append("*").append(detail.getNumber()).append(";");
        }
        return sb.toString();
    }
}
