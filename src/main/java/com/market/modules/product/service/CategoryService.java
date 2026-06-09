package com.market.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.modules.product.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有启用的分类树
     */
    List<Category> getCategoryTree();

    /**
     * 获取子分类列表
     */
    List<Category> getChildren(Long parentId);
}
