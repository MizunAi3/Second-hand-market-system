package com.market.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.market.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /** 用户名 */
    private String username;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 密码哈希 */
    private String passwordHash;

    /** 头像URL */
    private String avatar;

    /** 昵称 */
    private String nickname;

    /** 个人简介 */
    private String bio;

    /** 角色: USER / ADMIN */
    private String role;

    /** 状态: 0正常 1禁用 */
    private Integer status;

    /** 信誉分 */
    private Integer creditScore;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;
}
