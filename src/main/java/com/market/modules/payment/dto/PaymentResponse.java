package com.market.modules.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付响应
 */
@Data
@AllArgsConstructor
@Schema(description = "支付响应")
public class PaymentResponse {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "交易流水号")
    private String transactionId;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "提示信息")
    private String message;
}
