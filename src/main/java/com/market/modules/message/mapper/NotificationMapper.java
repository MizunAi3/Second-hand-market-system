package com.market.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.modules.message.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知 Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
