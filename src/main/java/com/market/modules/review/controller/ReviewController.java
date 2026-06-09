package com.market.modules.review.controller;

import com.market.common.PageResult;
import com.market.common.Result;
import com.market.modules.review.dto.ReviewRequest;
import com.market.modules.review.dto.ReviewVO;
import com.market.modules.review.service.ReviewService;
import com.market.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 */
@Tag(name = "评价接口", description = "交易评价与信誉分")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "发表评价")
    @PostMapping
    public Result<ReviewVO> createReview(@Valid @RequestBody ReviewRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ReviewVO vo = reviewService.createReview(userId, request);
        return Result.ok("评价成功", vo);
    }

    @Operation(summary = "获取用户收到的评价")
    @GetMapping("/user/{userId}")
    public Result<PageResult<ReviewVO>> getUserReviews(
            @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        PageResult<ReviewVO> result = reviewService.getUserReviews(userId, page, size);
        return Result.ok(result);
    }

    @Operation(summary = "获取用户信誉分")
    @GetMapping("/user/{userId}/score")
    public Result<Double> getCreditScore(@PathVariable Long userId) {
        Double score = reviewService.getUserCreditScore(userId);
        return Result.ok(score);
    }
}
