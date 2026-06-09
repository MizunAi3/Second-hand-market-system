package com.market.modules.product.controller;

import com.market.common.PageResult;
import com.market.common.Result;
import com.market.modules.product.dto.ProductRequest;
import com.market.modules.product.dto.ProductSearchRequest;
import com.market.modules.product.dto.ProductVO;
import com.market.modules.product.service.ProductService;
import com.market.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@Tag(name = "商品接口", description = "商品发布、浏览、搜索、收藏")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "发布商品")
    @PostMapping
    public Result<ProductVO> publish(@Valid @RequestBody ProductRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ProductVO vo = productService.publish(userId, request);
        return Result.ok("发布成功", vo);
    }

    @Operation(summary = "编辑商品")
    @PutMapping("/{id}")
    public Result<ProductVO> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ProductVO vo = productService.update(userId, id, request);
        return Result.ok("修改成功", vo);
    }

    @Operation(summary = "下架商品")
    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.offShelf(userId, id);
        return Result.ok("已下架");
    }

    @Operation(summary = "重新上架商品")
    @PutMapping("/{id}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.onShelf(userId, id);
        return Result.ok("已上架");
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> getDetail(@PathVariable Long id) {
        ProductVO vo = productService.getDetail(id);
        // 附加收藏状态（如果用户已登录）
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            vo.setIsFavorited(productService.isFavorited(userId, id));
        }
        return Result.ok(vo);
    }

    @Operation(summary = "搜索商品")
    @GetMapping
    public Result<PageResult<ProductVO>> search(ProductSearchRequest request) {
        PageResult<ProductVO> result = productService.search(request);
        return Result.ok(result);
    }

    @Operation(summary = "我的发布")
    @GetMapping("/my")
    public Result<PageResult<ProductVO>> getMyProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "状态: 1在售 2已售 3已下架") @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResult<ProductVO> result = productService.getMyProducts(userId, page, size, status);
        return Result.ok(result);
    }

    @Operation(summary = "收藏商品")
    @PostMapping("/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.favorite(userId, id);
        return Result.ok("收藏成功");
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{id}/favorite")
    public Result<Void> unfavorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.unfavorite(userId, id);
        return Result.ok("已取消收藏");
    }

    @Operation(summary = "我的收藏")
    @GetMapping("/favorites")
    public Result<PageResult<ProductVO>> getFavorites(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResult<ProductVO> result = productService.getFavorites(userId, page, size);
        return Result.ok(result);
    }
}
