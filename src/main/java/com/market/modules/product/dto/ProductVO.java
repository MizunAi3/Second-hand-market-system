package com.market.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情响应
 */
@Data
@Schema(description = "商品详情")
public class ProductVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "卖家ID")
    private Long sellerId;

    @Schema(description = "卖家昵称")
    private String sellerName;

    @Schema(description = "卖家头像")
    private String sellerAvatar;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "成色: 1全新 2几乎全新 3轻微使用 4明显使用")
    private Integer condition;

    @Schema(description = "状态: 1在售 2已售 3已下架")
    private Integer status;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;

    @Schema(description = "是否已收藏")
    private Boolean isFavorited;

    @Schema(description = "所在地")
    private String location;

    @Schema(description = "商品图片")
    private List<String> images;

    @Schema(description = "发布时间")
    private LocalDateTime createdAt;
}
