package com.market.modules.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.BusinessException;
import com.market.common.PageResult;
import com.market.common.ResultCode;
import com.market.modules.product.dto.ProductRequest;
import com.market.modules.product.dto.ProductSearchRequest;
import com.market.modules.product.dto.ProductVO;
import com.market.modules.product.entity.Category;
import com.market.modules.product.entity.Favorite;
import com.market.modules.product.entity.Product;
import com.market.modules.product.entity.ProductImage;
import com.market.modules.product.mapper.*;
import com.market.modules.product.service.ProductService;
import com.market.modules.user.dto.UserVO;
import com.market.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductImageMapper productImageMapper;
    private final FavoriteMapper favoriteMapper;
    private final CategoryMapper categoryMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ProductVO publish(Long sellerId, ProductRequest request) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setCategoryId(request.getCategoryId());
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCondition(request.getCondition());
        product.setLocation(request.getLocation());
        product.setStatus(1); // 在售
        product.setViewCount(0);
        product.setFavoriteCount(0);

        save(product);

        // 保存商品图片
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProductId(product.getId());
                image.setUrl(request.getImages().get(i));
                image.setSortOrder(i);
                productImageMapper.insert(image);
            }
        }

        return toVO(product);
    }

    @Override
    @Transactional
    public ProductVO update(Long sellerId, Long productId, ProductRequest request) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_OWNER);
        }

        product.setCategoryId(request.getCategoryId());
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCondition(request.getCondition());
        product.setLocation(request.getLocation());
        updateById(product);

        // 更新图片：先删后插
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId));
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProductId(productId);
                image.setUrl(request.getImages().get(i));
                image.setSortOrder(i);
                productImageMapper.insert(image);
            }
        }

        return toVO(product);
    }

    @Override
    @Transactional
    public void offShelf(Long sellerId, Long productId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_OWNER);
        }
        product.setStatus(3); // 下架
        updateById(product);
    }

    @Override
    @Transactional
    public void onShelf(Long sellerId, Long productId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        if (!product.getSellerId().equals(sellerId)) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_OWNER);
        }
        product.setStatus(1); // 在售
        updateById(product);
    }

    @Override
    public ProductVO getDetail(Long productId) {
        Product product = getById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        // 增加浏览量
        baseMapper.incrViewCount(productId);
        product.setViewCount(product.getViewCount() + 1);

        return toVO(product);
    }

    @Override
    public PageResult<ProductVO> search(ProductSearchRequest request) {
        Page<Product> page = new Page<>(request.getPage(), request.getSize());
        IPage<Product> result = baseMapper.searchProducts(
                page,
                request.getKeyword(),
                request.getCategoryId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getCondition(),
                request.getLocation(),
                request.getSortField(),
                request.getSortOrder()
        );

        List<ProductVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<ProductVO> getMyProducts(Long sellerId, Integer page, Integer size, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getSellerId, sellerId)
                .orderByDesc(Product::getCreatedAt);

        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        Page<Product> p = new Page<>(page, size);
        IPage<Product> result = page(p, wrapper);

        List<ProductVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional
    public void favorite(Long userId, Long productId) {
        Product product = getById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 不能收藏自己的商品
        if (product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能收藏自己的商品");
        }

        // 检查是否已收藏
        if (isFavorited(userId, productId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);

        baseMapper.incrFavoriteCount(productId);
    }

    @Override
    @Transactional
    public void unfavorite(Long userId, Long productId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));

        baseMapper.decrFavoriteCount(productId);
    }

    @Override
    public PageResult<ProductVO> getFavorites(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Favorite> favWrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> favPage = new Page<>(page, size);
        IPage<Favorite> favResult = favoriteMapper.selectPage(favPage, favWrapper);

        List<Long> productIds = favResult.getRecords().stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return PageResult.of(Collections.emptyList(), 0, page, size);
        }

        List<Product> products = listByIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<ProductVO> voList = favResult.getRecords().stream()
                .map(fav -> {
                    Product p = productMap.get(fav.getProductId());
                    return p != null ? toVO(p) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return PageResult.of(voList, favResult.getTotal(), favResult.getCurrent(), favResult.getSize());
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        if (userId == null) return false;
        return favoriteMapper.exists(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
    }

    /**
     * 填充 VO
     */
    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtil.copyProperties(product, vo);

        // 分类名称
        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 卖家信息
        UserVO seller = userService.getUserVO(product.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname() != null ? seller.getNickname() : seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }

        // 商品图片
        List<ProductImage> images = productImageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, product.getId())
                        .orderByAsc(ProductImage::getSortOrder));
        vo.setImages(images.stream().map(ProductImage::getUrl).collect(Collectors.toList()));

        return vo;
    }
}
