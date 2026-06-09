package com.market.modules.admin.service;

import java.util.Map;

/**
 * 管理后台服务接口
 */
public interface AdminService {

    /**
     * 禁用/启用用户
     */
    void toggleUserStatus(Long userId, Integer status);

    /**
     * 强制下架商品
     */
    void forceOffShelf(Long productId);

    /**
     * 获取平台统计数据
     */
    Map<String, Object> getStatistics();
}
