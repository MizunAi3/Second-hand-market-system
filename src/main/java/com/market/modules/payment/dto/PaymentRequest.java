package com.market.modules.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 支付请求
 */
@Data
@Schema(description = "支付请求")
public class PaymentRequest {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式: ALIPAY / WECHAT", example = "ALIPAY")
    private String paymentMethod;
}
