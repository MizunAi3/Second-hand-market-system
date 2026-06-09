package com.market.modules.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体
 */
@Data
@TableName("review")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 评价人ID */
    private Long reviewerId;

    /** 被评价人ID */
    private Long revieweeId;

    /** 评分: 1-5 */
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 评价图片, 逗号分隔 */
    private String images;

    /** 评价时间 */
    private LocalDateTime createdAt;
}
