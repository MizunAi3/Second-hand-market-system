package com.market.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.PageResult;
import com.market.modules.message.entity.Notification;
import com.market.modules.message.mapper.NotificationMapper;
import com.market.modules.message.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知服务实现
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void createNotification(Long userId, String type, String title, String content, String refId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRefId(refId);
        notification.setIsRead(0);
        save(notification);
    }

    @Override
    public PageResult<Notification> getNotifications(Long userId, Integer page, Integer size) {
        Page<Notification> p = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);

        IPage<Notification> result = page(p, wrapper);
        return PageResult.of(result);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return lambdaQuery()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .count();
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = getById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            updateById(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        lambdaUpdate()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1)
                .update();
    }
}
