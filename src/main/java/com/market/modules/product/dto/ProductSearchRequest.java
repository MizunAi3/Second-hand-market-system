package com.market.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品搜索请求
 */
@Data
@Schema(description = "商品搜索请求")
public class ProductSearchRequest {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "成色: 1全新 2几乎全新 3轻微使用 4明显使用")
    private Integer condition;

    @Schema(description = "所在地")
    private String location;

    @Schema(description = "排序字段: created_at / price / view_count", example = "created_at")
    private String sortField;

    @Schema(description = "排序方式: asc / desc", example = "desc")
    private String sortOrder;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer size = 10;
}
