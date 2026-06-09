package com.market.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.common.PageResult;
import com.market.modules.order.dto.CreateOrderRequest;
import com.market.modules.order.dto.OrderVO;
import com.market.modules.order.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    OrderVO createOrder(Long buyerId, CreateOrderRequest request);

    /**
     * 支付订单（支付成功后调用）
     */
    void payOrder(Long orderId, String paymentMethod);

    /**
     * 卖家发货
     */
    void shipOrder(Long sellerId, Long orderId);

    /**
     * 买家确认收货
     */
    void confirmReceive(Long buyerId, Long orderId);

    /**
     * 取消订单（仅待付款状态可取消）
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 申请退款
     */
    void requestRefund(Long userId, Long orderId);

    /**
     * 卖家同意退款
     */
    void approveRefund(Long sellerId, Long orderId);

    /**
     * 订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 我的订单（作为买家）
     */
    PageResult<OrderVO> getBuyerOrders(Long buyerId, Integer page, Integer size, Integer status);

    /**
     * 我的订单（作为卖家）
     */
    PageResult<OrderVO> getSellerOrders(Long sellerId, Integer page, Integer size, Integer status);
}
