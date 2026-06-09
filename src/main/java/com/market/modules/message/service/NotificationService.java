package com.market.modules.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.common.PageResult;
import com.market.modules.message.entity.Notification;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 创建通知
     */
    void createNotification(Long userId, String type, String title, String content, String refId);

    /**
     * 获取用户通知列表
     */
    PageResult<Notification> getNotifications(Long userId, Integer page, Integer size);

    /**
     * 获取未读通知数
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 全部标记为已读
     */
    void markAllAsRead(Long userId);
}
