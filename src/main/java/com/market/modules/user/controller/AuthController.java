package com.market.modules.user.controller;

import com.market.common.Result;
import com.market.modules.user.dto.LoginRequest;
import com.market.modules.user.dto.LoginResponse;
import com.market.modules.user.dto.RegisterRequest;
import com.market.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器 — 登录/注册（公开接口）
 */
@Tag(name = "认证接口", description = "用户注册与登录")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = userService.register(request);
        return Result.ok("注册成功", response);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.ok("登录成功", response);
    }
}
