package com.market.modules.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价响应
 */
@Data
@Schema(description = "评价")
public class ReviewVO {

    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "评价人ID")
    private Long reviewerId;

    @Schema(description = "评价人昵称")
    private String reviewerName;

    @Schema(description = "评价人头像")
    private String reviewerAvatar;

    @Schema(description = "被评价人ID")
    private Long revieweeId;

    @Schema(description = "评分")
    private Integer rating;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价图片")
    private List<String> images;

    @Schema(description = "评价时间")
    private LocalDateTime createdAt;
}
