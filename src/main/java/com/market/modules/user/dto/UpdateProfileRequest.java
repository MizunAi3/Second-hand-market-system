package com.market.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新个人信息请求
 */
@Data
@Schema(description = "更新个人信息请求")
public class UpdateProfileRequest {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "邮箱")
    private String email;
}
