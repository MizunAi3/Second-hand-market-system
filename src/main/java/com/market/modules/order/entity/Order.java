package com.market.modules.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.market.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`order`")
public class Order extends BaseEntity {

    /** 订单编号 */
    private String orderNo;

    /** 买家ID */
    private Long buyerId;

    /** 卖家ID */
    private Long sellerId;

    /** 商品ID */
    private Long productId;

    /** 订单金额 */
    private BigDecimal amount;

    /** 状态: 1待付款 2已付款 3已发货 4已收货 5已完成 6已取消 7退款中 8已退款 */
    private Integer status;

    /** 支付方式: ALIPAY / WECHAT */
    private String paymentMethod;

    /** 买家备注 */
    private String remark;

    /** 支付时间 */
    private LocalDateTime paidAt;

    /** 发货时间 */
    private LocalDateTime shippedAt;

    /** 收货时间 */
    private LocalDateTime receivedAt;

    /** 完成时间 */
    private LocalDateTime completedAt;

    /** 取消时间 */
    private LocalDateTime canceledAt;
}
