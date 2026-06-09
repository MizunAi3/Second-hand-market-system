package com.market.modules.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话ID */
    private String conversationId;

    /** 发送者ID */
    private Long fromUserId;

    /** 接收者ID */
    private Long toUserId;

    /** 消息内容 */
    private String content;

    /** 消息类型: 1文本 2图片 3系统消息 */
    private Integer messageType;

    /** 是否已读: 0未读 1已读 */
    private Integer isRead;

    /** 阅读时间 */
    private LocalDateTime readAt;

    /** 发送时间 */
    private LocalDateTime createdAt;
}
