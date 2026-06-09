package com.market.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水实体
 */
@Data
@TableName("payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 付款人ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付方式 */
    private String paymentMethod;

    /** 第三方交易号 */
    private String transactionId;

    /** 状态: 1待支付 2支付成功 3支付失败 */
    private Integer status;

    /** 支付时间 */
    private LocalDateTime paidAt;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
