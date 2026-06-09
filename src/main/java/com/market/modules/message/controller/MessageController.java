package com.market.modules.message.controller;

import com.market.common.PageResult;
import com.market.common.Result;
import com.market.modules.message.dto.MessageVO;
import com.market.modules.message.service.MessageService;
import com.market.modules.message.service.NotificationService;
import com.market.modules.message.entity.Notification;
import com.market.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息与通知控制器
 */
@Tag(name = "消息接口", description = "即时通讯消息与系统通知")
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final NotificationService notificationService;

    // ==================== 聊天消息 ====================

    @Operation(summary = "获取与某用户的聊天记录")
    @GetMapping("/chat/{targetUserId}")
    public Result<PageResult<MessageVO>> getChatMessages(
            @PathVariable Long targetUserId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "50") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 自动标记已读
        messageService.markAsRead(userId, targetUserId);

        PageResult<MessageVO> result = messageService.getConversationMessages(userId, targetUserId, page, size);
        return Result.ok(result);
    }

    @Operation(summary = "获取会话列表")
    @GetMapping("/conversations")
    public Result<List<MessageVO>> getConversations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MessageVO> list = messageService.getConversations(userId);
        return Result.ok(list);
    }

    @Operation(summary = "获取未读消息数")
    @GetMapping("/unread")
    public Result<Map<String, Long>> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        long msgCount = messageService.getUnreadCount(userId);
        long notifyCount = notificationService.getUnreadCount(userId);
        return Result.ok(Map.of(
                "messageCount", msgCount,
                "notificationCount", notifyCount
        ));
    }

    // ==================== 系统通知 ====================

    @Operation(summary = "获取通知列表")
    @GetMapping("/notifications")
    public Result<PageResult<Notification>> getNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResult<Notification> result = notificationService.getNotifications(userId, page, size);
        return Result.ok(result);
    }

    @Operation(summary = "标记通知已读")
    @PutMapping("/notifications/{id}/read")
    public Result<Void> markNotificationRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(userId, id);
        return Result.ok();
    }

    @Operation(summary = "全部通知标记已读")
    @PutMapping("/notifications/read-all")
    public Result<Void> markAllNotificationsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return Result.ok();
    }
}
