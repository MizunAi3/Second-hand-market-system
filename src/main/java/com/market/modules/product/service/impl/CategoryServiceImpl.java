package com.market.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.modules.product.entity.Category;
import com.market.modules.product.mapper.CategoryMapper;
import com.market.modules.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryTree() {
        // 查所有启用的分类
        List<Category> all = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));
        return all;
    }

    @Override
    public List<Category> getChildren(Long parentId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder));
    }
}
