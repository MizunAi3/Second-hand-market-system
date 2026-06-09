package com.market.security;

import com.market.modules.user.entity.User;
import com.market.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * UserDetailsService 实现 — 根据用户 ID 或用户名加载用户信息
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;

        // 尝试按 ID 查询
        try {
            Long id = Long.valueOf(username);
            user = userMapper.selectById(id);
        } catch (NumberFormatException e) {
            // 按手机号或邮箱查询
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, username)
                    .or()
                    .eq(User::getEmail, username));
        }

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 1) {
            throw new UsernameNotFoundException("账号已被禁用");
        }

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
