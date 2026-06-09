package com.market.modules.product.controller;

import com.market.common.Result;
import com.market.modules.product.entity.Category;
import com.market.modules.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器（公开接口）
 */
@Tag(name = "分类接口", description = "商品分类浏览")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树")
    @GetMapping
    public Result<List<Category>> getCategoryTree() {
        List<Category> list = categoryService.getCategoryTree();
        return Result.ok(list);
    }

    @Operation(summary = "获取子分类")
    @GetMapping("/{parentId}/children")
    public Result<List<Category>> getChildren(@PathVariable Long parentId) {
        List<Category> list = categoryService.getChildren(parentId);
        return Result.ok(list);
    }
}
