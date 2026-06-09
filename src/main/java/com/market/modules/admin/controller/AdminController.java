package com.market.modules.admin.controller;

import com.market.common.Result;
import com.market.modules.admin.service.AdminService;
import com.market.modules.product.dto.ProductSearchRequest;
import com.market.modules.product.dto.ProductVO;
import com.market.modules.product.service.ProductService;
import com.market.modules.user.entity.User;
import com.market.modules.user.service.UserService;
import com.market.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台控制器（需 ADMIN 角色）
 */
@Tag(name = "管理后台", description = "用户管理、商品审核、数据统计")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final ProductService productService;

    // ==================== 用户管理 ====================

    @Operation(summary = "用户列表")
    @GetMapping("/users")
    public Result<PageResult<User>> getUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> p =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .orderByDesc(User::getCreatedAt);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getEmail, keyword));
        }

        var result = userService.page(p, wrapper);
        return Result.ok(PageResult.of(result));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/users/{id}/status")
    public Result<Void> toggleUserStatus(
            @PathVariable Long id,
            @Parameter(description = "状态: 0正常 1禁用") @RequestParam Integer status) {
        adminService.toggleUserStatus(id, status);
        return Result.ok(status == 1 ? "已禁用" : "已启用");
    }

    // ==================== 商品管理 ====================

    @Operation(summary = "所有商品列表（含已下架）")
    @GetMapping("/products")
    public Result<PageResult<ProductVO>> getAllProducts(
            ProductSearchRequest request) {
        PageResult<ProductVO> result = productService.search(request);
        return Result.ok(result);
    }

    @Operation(summary = "强制下架商品")
    @PutMapping("/products/{id}/off-shelf")
    public Result<Void> forceOffShelf(@PathVariable Long id) {
        adminService.forceOffShelf(id);
        return Result.ok("已强制下架");
    }

    // ==================== 数据统计 ====================

    @Operation(summary = "平台数据统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = adminService.getStatistics();
        return Result.ok(stats);
    }
}
