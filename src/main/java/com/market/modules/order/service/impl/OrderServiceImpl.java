package com.market.modules.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.BusinessException;
import com.market.common.PageResult;
import com.market.common.ResultCode;
import com.market.modules.order.dto.CreateOrderRequest;
import com.market.modules.order.dto.OrderVO;
import com.market.modules.order.entity.Order;
import com.market.modules.order.entity.OrderLog;
import com.market.modules.order.mapper.OrderLogMapper;
import com.market.modules.order.mapper.OrderMapper;
import com.market.modules.order.service.OrderService;
import com.market.modules.product.entity.Product;
import com.market.modules.product.entity.ProductImage;
import com.market.modules.product.mapper.ProductImageMapper;
import com.market.modules.product.service.ProductService;
import com.market.modules.user.dto.UserVO;
import com.market.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderLogMapper orderLogMapper;
    private final ProductService productService;
    private final ProductImageMapper productImageMapper;
    private final UserService userService;

    @Override
    @Transactional
    public OrderVO createOrder(Long buyerId, CreateOrderRequest request) {
        Product product = productService.getById(request.getProductId());
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (product.getSellerId().equals(buyerId)) {
            throw new BusinessException("不能购买自己的商品");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setProductId(product.getId());
        order.setAmount(product.getPrice());
        order.setStatus(1); // 待付款
        order.setRemark(request.getRemark());

        save(order);

        // 锁定商品状态
        product.setStatus(2); // 已售
        productService.updateById(product);

        // 记录日志
        addOrderLog(order.getId(), order.getOrderNo(), "CREATE", buyerId, "创建订单");

        return toVO(order);
    }

    @Override
    @Transactional
    public void payOrder(Long orderId, String paymentMethod) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(2); // 已付款
        order.setPaymentMethod(paymentMethod);
        order.setPaidAt(LocalDateTime.now());
        updateById(order);

        addOrderLog(order.getId(), order.getOrderNo(), "PAY", order.getBuyerId(), "付款成功");
    }

    @Override
    @Transactional
    public void shipOrder(Long sellerId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(3); // 已发货
        order.setShippedAt(LocalDateTime.now());
        updateById(order);

        addOrderLog(order.getId(), order.getOrderNo(), "SHIP", sellerId, "卖家发货");
    }

    @Override
    @Transactional
    public void confirmReceive(Long buyerId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }
        if (order.getStatus() != 3) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(4); // 已收货
        order.setReceivedAt(LocalDateTime.now());
        order.setCompletedAt(LocalDateTime.now());
        order.setStatus(5); // 直接完成
        updateById(order);

        addOrderLog(order.getId(), order.getOrderNo(), "CONFIRM", buyerId, "确认收货，订单完成");
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus(6); // 已取消
        order.setCanceledAt(LocalDateTime.now());
        updateById(order);

        // 恢复商品状态
        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(1); // 重新在售
            productService.updateById(product);
        }

        addOrderLog(order.getId(), order.getOrderNo(), "CANCEL", userId, "取消订单");
    }

    @Override
    @Transactional
    public void requestRefund(Long userId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }
        // 已付款或已发货状态可申请退款
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(7); // 退款中
        updateById(order);

        addOrderLog(order.getId(), order.getOrderNo(), "REFUND_REQUEST", userId, "申请退款");
    }

    @Override
    @Transactional
    public void approveRefund(Long sellerId, Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }
        if (order.getStatus() != 7) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        order.setStatus(8); // 已退款
        updateById(order);

        // 恢复商品
        Product product = productService.getById(order.getProductId());
        if (product != null) {
            product.setStatus(1);
            productService.updateById(product);
        }

        addOrderLog(order.getId(), order.getOrderNo(), "REFUND_APPROVE", sellerId, "同意退款");
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return toVO(order);
    }

    @Override
    public PageResult<OrderVO> getBuyerOrders(Long buyerId, Integer page, Integer size, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerId, buyerId)
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        Page<Order> p = new Page<>(page, size);
        IPage<Order> result = page(p, wrapper);

        List<OrderVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<OrderVO> getSellerOrders(Long sellerId, Integer page, Integer size, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getSellerId, sellerId)
                .orderByDesc(Order::getCreatedAt);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        Page<Order> p = new Page<>(page, size);
        IPage<Order> result = page(p, wrapper);

        List<OrderVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    // ---- 内部方法 ----

    private String generateOrderNo() {
        return IdUtil.getSnowflakeNextIdStr();
    }

    private void addOrderLog(Long orderId, String orderNo, String action, Long operatorId, String remark) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setAction(action);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        orderLogMapper.insert(log);
    }

    private OrderVO toVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);

        // 状态文本
        vo.setStatusText(getStatusText(order.getStatus()));

        // 商品信息
        Product product = productService.getById(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            // 取第一张图片
            List<ProductImage> images = productImageMapper.selectList(
                    new LambdaQueryWrapper<ProductImage>()
                            .eq(ProductImage::getProductId, product.getId())
                            .orderByAsc(ProductImage::getSortOrder)
                            .last("LIMIT 1"));
            if (!images.isEmpty()) {
                vo.setProductImage(images.get(0).getUrl());
            }
        }

        // 买家/卖家信息
        UserVO buyer = userService.getUserVO(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getNickname() != null ? buyer.getNickname() : buyer.getUsername());
        }
        UserVO seller = userService.getUserVO(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname() != null ? seller.getNickname() : seller.getUsername());
        }

        return vo;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 1 -> "待付款";
            case 2 -> "已付款";
            case 3 -> "已发货";
            case 4 -> "已收货";
            case 5 -> "已完成";
            case 6 -> "已取消";
            case 7 -> "退款中";
            case 8 -> "已退款";
            default -> "未知";
        };
    }
}
