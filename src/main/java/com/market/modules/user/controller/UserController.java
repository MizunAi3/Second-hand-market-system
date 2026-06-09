package com.market.modules.user.controller;

import com.market.common.Result;
import com.market.modules.user.dto.ChangePasswordRequest;
import com.market.modules.user.dto.UpdateProfileRequest;
import com.market.modules.user.dto.UserVO;
import com.market.modules.user.service.UserService;
import com.market.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 — 个人信息管理（需认证）
 */
@Tag(name = "用户接口", description = "个人信息管理")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserVO vo = userService.getProfile(userId);
        return Result.ok(vo);
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateProfile(userId, request);
        return Result.ok();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, request);
        return Result.ok("密码修改成功");
    }

    @Operation(summary = "获取用户公开信息")
    @GetMapping("/info/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        UserVO vo = userService.getUserVO(userId);
        return Result.ok(vo);
    }
}
