package com.market.modules.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.PageResult;
import com.market.modules.message.dto.MessageVO;
import com.market.modules.message.entity.Message;
import com.market.modules.message.mapper.MessageMapper;
import com.market.modules.message.service.MessageService;
import com.market.modules.user.dto.UserVO;
import com.market.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息服务实现
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final UserService userService;

    @Override
    public Message sendMessage(Long fromUserId, Long toUserId, String conversationId, String content, Integer messageType) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : 1);
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        save(message);
        return message;
    }

    @Override
    public PageResult<MessageVO> getConversationMessages(Long userId, Long targetUserId, Integer page, Integer size) {
        String conversationId = userId < targetUserId
                ? userId + "_" + targetUserId
                : targetUserId + "_" + userId;

        Page<Message> p = new Page<>(page, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getCreatedAt);

        IPage<Message> result = page(p, wrapper);

        List<MessageVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        Collections.reverse(voList); // 倒序让最新消息在底部

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<MessageVO> getConversations(Long userId) {
        // 获取每个会话的最新一条消息，按发送者分组，按时间倒序
        List<Message> messages = baseMapper.selectList(new LambdaQueryWrapper<Message>()
                .and(w -> w.eq(Message::getFromUserId, userId).or().eq(Message::getToUserId, userId))
                .orderByDesc(Message::getCreatedAt));

        // 按会话去重，只保留最新一条
        return messages.stream()
                .collect(Collectors.toMap(
                        Message::getConversationId,
                        m -> m,
                        (existing, replacement) -> existing.getCreatedAt().isAfter(replacement.getCreatedAt())
                                ? existing : replacement))
                .values()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long userId, Long targetUserId) {
        String conversationId = userId < targetUserId
                ? userId + "_" + targetUserId
                : targetUserId + "_" + userId;

        List<Message> unread = list(new LambdaQueryWrapper<Message>()
                .eq(Message::getConversationId, conversationId)
                .eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0));

        for (Message msg : unread) {
            msg.setIsRead(1);
            msg.setReadAt(LocalDateTime.now());
            updateById(msg);
        }
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return lambdaQuery()
                .eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0)
                .count();
    }

    private MessageVO toVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtil.copyProperties(message, vo);

        UserVO fromUser = userService.getUserVO(message.getFromUserId());
        if (fromUser != null) {
            vo.setFromUserName(fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getUsername());
            vo.setFromUserAvatar(fromUser.getAvatar());
        }

        return vo;
    }
}
