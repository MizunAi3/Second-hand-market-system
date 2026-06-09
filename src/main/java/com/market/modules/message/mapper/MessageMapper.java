package com.market.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.modules.message.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息 Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
