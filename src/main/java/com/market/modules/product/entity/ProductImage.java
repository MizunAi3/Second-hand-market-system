package com.market.modules.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品图片实体
 */
@Data
@TableName("product_image")
public class ProductImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品ID */
    private Long productId;

    /** 图片URL */
    private String url;

    /** 排序 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
