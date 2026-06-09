package com.market.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类 — 获取当前登录用户信息
 */
@Component
public class SecurityUtils {

    /**
     * 获取当前登录用户 ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return Long.valueOf(user.getUsername());
        }
        return null;
    }

    /**
     * 获取当前登录用户名（这里即用户 ID 的字符串形式）
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
}
