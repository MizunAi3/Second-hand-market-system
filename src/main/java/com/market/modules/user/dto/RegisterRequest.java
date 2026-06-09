package com.market.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求
 */
@Data
@Schema(description = "注册请求")
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 64, message = "用户名长度为2-64个字符")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为6-32个字符")
    @Schema(description = "密码", example = "123456")
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", example = "13800000001")
    private String phone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
}
