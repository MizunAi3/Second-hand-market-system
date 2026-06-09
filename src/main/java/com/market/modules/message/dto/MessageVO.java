package com.market.modules.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息响应
 */
@Data
@Schema(description = "消息")
public class MessageVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "发送者ID")
    private Long fromUserId;

    @Schema(description = "发送者昵称")
    private String fromUserName;

    @Schema(description = "发送者头像")
    private String fromUserAvatar;

    @Schema(description = "接收者ID")
    private Long toUserId;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型: 1文本 2图片 3系统消息")
    private Integer messageType;

    @Schema(description = "是否已读")
    private Integer isRead;

    @Schema(description = "发送时间")
    private LocalDateTime createdAt;
}
