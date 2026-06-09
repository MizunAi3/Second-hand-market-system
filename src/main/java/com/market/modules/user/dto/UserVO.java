package com.market.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "信誉分")
    private Integer creditScore;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;
}
