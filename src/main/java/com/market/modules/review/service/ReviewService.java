package com.market.modules.review.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.market.common.PageResult;
import com.market.modules.review.dto.ReviewRequest;
import com.market.modules.review.dto.ReviewVO;
import com.market.modules.review.entity.Review;

/**
 * 评价服务接口
 */
public interface ReviewService extends IService<Review> {

    /**
     * 发表评价
     */
    ReviewVO createReview(Long reviewerId, ReviewRequest request);

    /**
     * 获取用户的评价列表（收到的评价）
     */
    PageResult<ReviewVO> getUserReviews(Long userId, Integer page, Integer size);

    /**
     * 获取用户的信誉分
     */
    Double getUserCreditScore(Long userId);
}
