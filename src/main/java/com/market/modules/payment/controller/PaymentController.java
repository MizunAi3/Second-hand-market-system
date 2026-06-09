package com.market.modules.payment.controller;

import com.market.common.Result;
import com.market.modules.payment.dto.PaymentRequest;
import com.market.modules.payment.dto.PaymentResponse;
import com.market.modules.payment.service.PaymentService;
import com.market.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付控制器（模拟支付）
 *
 * 正式上线时替换为：
 * - POST /api/v1/payment/create  → 创建预支付订单，返回支付链接/二维码
 * - POST /api/v1/payment/callback → 支付宝/微信支付异步回调（对外公开，需验签）
 */
@Tag(name = "支付接口", description = "模拟支付")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "模拟支付")
    @PostMapping("/pay")
    public Result<PaymentResponse> pay(@Valid @RequestBody PaymentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PaymentResponse response = paymentService.pay(userId, request);
        return Result.ok(response);
    }
}
