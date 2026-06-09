package com.market.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.modules.user.dto.*;
import com.market.modules.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    LoginResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 获取当前用户信息
     */
    UserVO getProfile(Long userId);

    /**
     * 获取用户公开信息（供其他模块调用）
     */
    UserVO getUserVO(Long userId);

    /**
     * 更新个人信息
     */
    void updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * 修改密码
     */
    void changePassword(Long userId, ChangePasswordRequest request);
}
