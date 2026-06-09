package com.market.modules.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.modules.payment.dto.PaymentRequest;
import com.market.modules.payment.dto.PaymentResponse;
import com.market.modules.payment.entity.PaymentRecord;

/**
 * 支付服务接口
 */
public interface PaymentService extends IService<PaymentRecord> {

    /**
     * 模拟支付
     */
    PaymentResponse pay(Long userId, PaymentRequest request);

    /**
     * 根据订单ID查询支付记录
     */
    PaymentRecord getByOrderId(Long orderId);
}
