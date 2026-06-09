package com.market.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单请求
 */
@Data
@Schema(description = "创建订单请求")
public class CreateOrderRequest {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", example = "1")
    private Long productId;

    @Schema(description = "买家备注")
    private String remark;
}
