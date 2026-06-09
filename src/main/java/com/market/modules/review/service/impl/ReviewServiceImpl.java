package com.market.modules.review.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.market.common.BusinessException;
import com.market.common.PageResult;
import com.market.common.ResultCode;
import com.market.modules.message.service.NotificationService;
import com.market.modules.order.entity.Order;
import com.market.modules.order.service.OrderService;
import com.market.modules.review.dto.ReviewRequest;
import com.market.modules.review.dto.ReviewVO;
import com.market.modules.review.entity.Review;
import com.market.modules.review.mapper.ReviewMapper;
import com.market.modules.review.service.ReviewService;
import com.market.modules.user.dto.UserVO;
import com.market.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价服务实现
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final OrderService orderService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ReviewVO createReview(Long reviewerId, ReviewRequest request) {
        Order order = orderService.getById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 5) {
            throw new BusinessException(ResultCode.REVIEW_NOT_ALLOWED, "订单未完成，暂不能评价");
        }

        // 确定被评价人
        Long revieweeId;
        if (order.getBuyerId().equals(reviewerId)) {
            revieweeId = order.getSellerId(); // 买家评价卖家
        } else if (order.getSellerId().equals(reviewerId)) {
            revieweeId = order.getBuyerId(); // 卖家评价买家
        } else {
            throw new BusinessException(ResultCode.ORDER_NOT_YOURS);
        }

        // 检查是否已评价
        if (lambdaQuery()
                .eq(Review::getOrderId, request.getOrderId())
                .eq(Review::getReviewerId, reviewerId)
                .count() > 0) {
            throw new BusinessException(ResultCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setReviewerId(reviewerId);
        review.setRevieweeId(revieweeId);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            review.setImages(String.join(",", request.getImages()));
        }
        save(review);

        // 发送通知
        notificationService.createNotification(
                revieweeId,
                "REVIEW",
                "收到新评价",
                "您收到了一条" + request.getRating() + "星评价",
                String.valueOf(order.getId())
        );

        return toVO(review);
    }

    @Override
    public PageResult<ReviewVO> getUserReviews(Long userId, Integer page, Integer size) {
        Page<Review> p = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getRevieweeId, userId)
                .orderByDesc(Review::getCreatedAt);

        IPage<Review> result = page(p, wrapper);

        List<ReviewVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Double getUserCreditScore(Long userId) {
        List<Review> reviews = list(new LambdaQueryWrapper<Review>()
                .eq(Review::getRevieweeId, userId));

        if (reviews.isEmpty()) {
            return 5.0;
        }

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(5.0);

        return Math.round(avg * 10.0) / 10.0;
    }

    private ReviewVO toVO(Review review) {
        ReviewVO vo = new ReviewVO();
        BeanUtil.copyProperties(review, vo);

        // 评价人信息
        UserVO reviewer = userService.getUserVO(review.getReviewerId());
        if (reviewer != null) {
            vo.setReviewerName(reviewer.getNickname() != null ? reviewer.getNickname() : reviewer.getUsername());
            vo.setReviewerAvatar(reviewer.getAvatar());
        }

        // 图片
        if (review.getImages() != null && !review.getImages().isEmpty()) {
            vo.setImages(Arrays.asList(review.getImages().split(",")));
        } else {
            vo.setImages(Collections.emptyList());
        }

        return vo;
    }
}
