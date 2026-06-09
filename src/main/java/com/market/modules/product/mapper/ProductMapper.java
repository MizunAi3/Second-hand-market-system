package com.market.modules.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.modules.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品 Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE product SET view_count = view_count + 1 WHERE id = #{id}")
    void incrViewCount(@Param("id") Long id);

    /**
     * 增加收藏次数
     */
    @Update("UPDATE product SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    void incrFavoriteCount(@Param("id") Long id);

    /**
     * 减少收藏次数
     */
    @Update("UPDATE product SET favorite_count = GREATEST(favorite_count - 1, 0) WHERE id = #{id}")
    void decrFavoriteCount(@Param("id") Long id);

    /**
     * 搜索商品（关键词 LIKE 匹配标题和描述）
     */
    IPage<Product> searchProducts(Page<Product> page,
                                  @Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  @Param("minPrice") java.math.BigDecimal minPrice,
                                  @Param("maxPrice") java.math.BigDecimal maxPrice,
                                  @Param("condition") Integer condition,
                                  @Param("location") String location,
                                  @Param("sortField") String sortField,
                                  @Param("sortOrder") String sortOrder);
}
