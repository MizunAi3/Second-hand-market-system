package com.market.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录/注册响应
 */
@Data
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "角色", example = "USER")
    private String role;
}
