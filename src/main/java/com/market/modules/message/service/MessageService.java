package com.market.modules.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.common.PageResult;
import com.market.modules.message.dto.MessageVO;
import com.market.modules.message.entity.Message;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 发送消息
     */
    Message sendMessage(Long fromUserId, Long toUserId, String conversationId, String content, Integer messageType);

    /**
     * 获取会话消息列表
     */
    PageResult<MessageVO> getConversationMessages(Long userId, Long targetUserId, Integer page, Integer size);

    /**
     * 获取会话列表
     */
    List<MessageVO> getConversations(Long userId);

    /**
     * 标记消息为已读
     */
    void markAsRead(Long userId, Long targetUserId);

    /**
     * 获取未读消息数
     */
    Long getUnreadCount(Long userId);
}
