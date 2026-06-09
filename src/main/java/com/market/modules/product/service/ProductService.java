package com.market.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.common.PageResult;
import com.market.modules.product.dto.ProductRequest;
import com.market.modules.product.dto.ProductSearchRequest;
import com.market.modules.product.dto.ProductVO;
import com.market.modules.product.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 发布商品
     */
    ProductVO publish(Long sellerId, ProductRequest request);

    /**
     * 编辑商品
     */
    ProductVO update(Long sellerId, Long productId, ProductRequest request);

    /**
     * 下架商品
     */
    void offShelf(Long sellerId, Long productId);

    /**
     * 重新上架
     */
    void onShelf(Long sellerId, Long productId);

    /**
     * 商品详情
     */
    ProductVO getDetail(Long productId);

    /**
     * 搜索商品列表
     */
    PageResult<ProductVO> search(ProductSearchRequest request);

    /**
     * 获取卖家发布的商品
     */
    PageResult<ProductVO> getMyProducts(Long sellerId, Integer page, Integer size, Integer status);

    /**
     * 收藏商品
     */
    void favorite(Long userId, Long productId);

    /**
     * 取消收藏
     */
    void unfavorite(Long userId, Long productId);

    /**
     * 获取收藏列表
     */
    PageResult<ProductVO> getFavorites(Long userId, Integer page, Integer size);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long productId);
}
