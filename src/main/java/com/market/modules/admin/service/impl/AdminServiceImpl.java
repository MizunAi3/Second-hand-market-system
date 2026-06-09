package com.market.modules.admin.service.impl;

import com.market.common.BusinessException;
import com.market.common.ResultCode;
import com.market.modules.admin.service.AdminService;
import com.market.modules.order.entity.Order;
import com.market.modules.order.mapper.OrderMapper;
import com.market.modules.product.entity.Product;
import com.market.modules.product.mapper.ProductMapper;
import com.market.modules.user.entity.User;
import com.market.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void forceOffShelf(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }
        product.setStatus(3); // 强制下架
        productMapper.updateById(product);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 用户总数
        stats.put("totalUsers", userMapper.selectCount(null));

        // 商品总数
        stats.put("totalProducts", productMapper.selectCount(null));

        // 在售商品数
        stats.put("activeProducts", productMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)));

        // 订单总数
        stats.put("totalOrders", orderMapper.selectCount(null));

        // 已完成订单数
        stats.put("completedOrders", orderMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 5)));

        // 总交易额
        Double totalAmount = orderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .in(Order::getStatus, 2, 3, 4, 5))
                .stream()
                .mapToDouble(o -> o.getAmount().doubleValue())
                .sum();
        stats.put("totalAmount", totalAmount);

        return stats;
    }
}
