package com.market.modules.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情响应
 */
@Data
@Schema(description = "订单详情")
public class OrderVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "买家ID")
    private Long buyerId;

    @Schema(description = "买家昵称")
    private String buyerName;

    @Schema(description = "卖家ID")
    private Long sellerId;

    @Schema(description = "卖家昵称")
    private String sellerName;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品标题")
    private String productTitle;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "订单金额")
    private BigDecimal amount;

    @Schema(description = "状态: 1待付款 2已付款 3已发货 4已收货 5已完成 6已取消 7退款中 8已退款")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "买家备注")
    private String remark;

    @Schema(description = "支付时间")
    private LocalDateTime paidAt;

    @Schema(description = "发货时间")
    private LocalDateTime shippedAt;

    @Schema(description = "收货时间")
    private LocalDateTime receivedAt;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "取消时间")
    private LocalDateTime canceledAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
