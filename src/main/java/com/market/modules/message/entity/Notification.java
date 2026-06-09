package com.market.modules.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 通知类型: ORDER / MESSAGE / SYSTEM / FAVORITE */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 关联业务ID */
    private String refId;

    /** 是否已读: 0未读 1已读 */
    private Integer isRead;

    /** 通知时间 */
    private LocalDateTime createdAt;
}
