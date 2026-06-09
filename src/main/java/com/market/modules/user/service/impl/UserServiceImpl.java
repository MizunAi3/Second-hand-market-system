package com.market.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.BusinessException;
import com.market.common.ResultCode;
import com.market.modules.user.dto.*;
import com.market.modules.user.entity.User;
import com.market.modules.user.mapper.UserMapper;
import com.market.modules.user.service.UserService;
import com.market.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 检查手机号是否已注册
        if (lambdaQuery().eq(User::getPhone, request.getPhone()).count() > 0) {
            throw new BusinessException(ResultCode.USER_PHONE_EXISTS);
        }
        // 检查邮箱是否已注册
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (lambdaQuery().eq(User::getEmail, request.getEmail()).count() > 0) {
                throw new BusinessException(ResultCode.USER_EMAIL_EXISTS);
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setStatus(0);
        user.setCreditScore(100);

        save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 按手机号或邮箱查找用户
        User user = lambdaQuery()
                .eq(User::getPhone, request.getAccount())
                .or()
                .eq(User::getEmail, request.getAccount())
                .one();

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_DISABLED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        updateById(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toVO(user);
    }

    @Override
    public UserVO getUserVO(Long userId) {
        if (userId == null) return null;
        User user = getById(userId);
        return user == null ? null : toVO(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getEmail() != null) {
            // 检查邮箱唯一性
            if (lambdaQuery().eq(User::getEmail, request.getEmail()).ne(User::getId, userId).count() > 0) {
                throw new BusinessException(ResultCode.USER_EMAIL_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        updateById(user);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo, "passwordHash");
        return vo;
    }
}
