package com.market.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单日志实体
 */
@Data
@TableName("order_log")
public class OrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 操作类型 */
    private String action;

    /** 操作人ID */
    private Long operatorId;

    /** 备注 */
    private String remark;

    /** 操作时间 */
    private LocalDateTime createdAt;
}
