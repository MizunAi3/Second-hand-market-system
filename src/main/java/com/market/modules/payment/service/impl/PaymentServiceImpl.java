package com.market.modules.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.BusinessException;
import com.market.common.ResultCode;
import com.market.modules.order.entity.Order;
import com.market.modules.order.service.OrderService;
import com.market.modules.payment.dto.PaymentRequest;
import com.market.modules.payment.dto.PaymentResponse;
import com.market.modules.payment.entity.PaymentRecord;
import com.market.modules.payment.mapper.PaymentRecordMapper;
import com.market.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 支付服务实现 — 模拟支付
 *
 * 正式上线时替换为真实支付宝/微信支付对接：
 * 1. 调用第三方支付 SDK 生成预支付订单
 * 2. 接收支付回调通知
 * 3. 验签后更新订单状态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentService {

    private final OrderService orderService;

    @Override
    @Transactional
    public PaymentResponse pay(Long userId, PaymentRequest request) {
        Order order = orderService.getById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ResultCode.PAYMENT_ALREADY_PAID);
        }

        // 生成模拟交易流水号
        String transactionId = "MOCK_" + IdUtil.getSnowflakeNextIdStr();

        // 创建支付记录
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setUserId(userId);
        record.setAmount(order.getAmount());
        record.setPaymentMethod(request.getPaymentMethod());
        record.setTransactionId(transactionId);
        record.setStatus(2); // 模拟支付直接成功
        record.setPaidAt(LocalDateTime.now());
        save(record);

        // 更新订单状态为已付款
        orderService.payOrder(order.getId(), request.getPaymentMethod());

        log.info("模拟支付成功: orderNo={}, transactionId={}, amount={}",
                order.getOrderNo(), transactionId, order.getAmount());

        return new PaymentResponse(true, transactionId, order.getAmount(), "支付成功（模拟）");
    }

    @Override
    public PaymentRecord getByOrderId(Long orderId) {
        return getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderId, orderId)
                .orderByDesc(PaymentRecord::getCreatedAt)
                .last("LIMIT 1"));
    }
}
