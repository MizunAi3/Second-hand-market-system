package com.market.modules.message.websocket;

import cn.hutool.json.JSONUtil;
import com.market.modules.message.entity.Message;
import com.market.modules.message.service.MessageService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 聊天服务端点
 *
 * 连接地址: ws://host:port/ws/chat/{userId}?token={jwtToken}
 */
@Slf4j
@Component
@ServerEndpoint("/ws/chat/{userId}")
public class ChatWebSocketServer {

    /** 在线用户: userId -> Session */
    private static final Map<Long, Session> onlineUsers = new ConcurrentHashMap<>();

    /** 由于 @ServerEndpoint 不是 Spring Bean，通过静态方式注入 */
    private static MessageService messageService;

    @Autowired
    public void setMessageService(MessageService service) {
        ChatWebSocketServer.messageService = service;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        onlineUsers.put(userId, session);
        log.info("用户上线: userId={}, 在线人数={}", userId, onlineUsers.size());
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId) {
        onlineUsers.remove(userId);
        log.info("用户下线: userId={}, 在线人数={}", userId, onlineUsers.size());
    }

    @OnMessage
    public void onMessage(String messageStr, Session session, @PathParam("userId") Long userId) {
        log.info("收到消息: fromUserId={}, content={}", userId, messageStr);

        try {
            // 消息格式: { "toUserId": 2, "content": "你好", "messageType": 1 }
            Map<String, Object> msgMap = JSONUtil.parseObj(messageStr);
            Long toUserId = Long.valueOf(msgMap.get("toUserId").toString());
            String content = msgMap.get("content").toString();
            Integer messageType = msgMap.containsKey("messageType") ?
                    Integer.valueOf(msgMap.get("messageType").toString()) : 1;

            // 生成会话ID: 小ID_大ID
            String conversationId = userId < toUserId
                    ? userId + "_" + toUserId
                    : toUserId + "_" + userId;

            // 保存消息到数据库
            Message message = messageService.sendMessage(userId, toUserId, conversationId, content, messageType);

            // 推送给接收者（如果在线）
            Session toSession = onlineUsers.get(toUserId);
            if (toSession != null && toSession.isOpen()) {
                String pushMsg = JSONUtil.toJsonStr(Map.of(
                        "id", message.getId(),
                        "fromUserId", userId,
                        "toUserId", toUserId,
                        "conversationId", conversationId,
                        "content", content,
                        "messageType", messageType,
                        "createdAt", message.getCreatedAt().toString()
                ));
                toSession.getBasicRemote().sendText(pushMsg);
            }

            // ACK 回执给发送者
            session.getBasicRemote().sendText(JSONUtil.toJsonStr(Map.of(
                    "type", "ack",
                    "messageId", message.getId(),
                    "status", "sent"
            )));

        } catch (Exception e) {
            log.error("消息处理异常", e);
            try {
                session.getBasicRemote().sendText(JSONUtil.toJsonStr(Map.of(
                        "type", "error",
                        "message", "消息发送失败: " + e.getMessage()
                )));
            } catch (IOException ignored) {}
        }
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") Long userId) {
        log.error("WebSocket 错误: userId={}", userId, error);
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * 推送消息给指定用户
     */
    public static void pushToUser(Long userId, String message) {
        Session session = onlineUsers.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("推送消息失败: userId={}", userId, e);
            }
        }
    }
}
