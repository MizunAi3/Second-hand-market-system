package com.market.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品发布/编辑请求
 */
@Data
@Schema(description = "商品请求")
public class ProductRequest {

    @NotBlank(message = "商品标题不能为空")
    @Schema(description = "商品标题", example = "iPhone 15 99新")
    private String title;

    @Schema(description = "商品描述", example = "使用三个月，无划痕，配件齐全")
    private String description;

    @NotNull(message = "分类不能为空")
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @NotNull(message = "价格不能为空")
    @Schema(description = "售价", example = "5999.00")
    private BigDecimal price;

    @Schema(description = "原价", example = "6999.00")
    private BigDecimal originalPrice;

    @NotNull(message = "成色不能为空")
    @Schema(description = "成色: 1全新 2几乎全新 3轻微使用 4明显使用", example = "2")
    private Integer condition;

    @Schema(description = "所在地", example = "北京")
    private String location;

    @Schema(description = "商品图片URL列表")
    private List<String> images;
}
