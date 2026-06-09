package com.market.modules.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.market.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    /** 分类名称 */
    private String name;

    /** 父分类ID, 0为顶级分类 */
    private Long parentId;

    /** 分类图标 */
    private String icon;

    /** 排序 */
    private Integer sortOrder;

    /** 状态: 0禁用 1启用 */
    private Integer status;
}
