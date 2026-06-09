package com.market.modules.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.market.modules.product.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏 Mapper
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
