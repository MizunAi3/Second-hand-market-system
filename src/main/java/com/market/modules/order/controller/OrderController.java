package com.market.modules.order.controller;

import com.market.common.PageResult;
import com.market.common.Result;
import com.market.modules.order.dto.CreateOrderRequest;
import com.market.modules.order.dto.OrderVO;
import com.market.modules.order.service.OrderService;
import com.market.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单接口", description = "订单创建、状态流转与管理")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderVO vo = orderService.createOrder(userId, request);
        return Result.ok("下单成功", vo);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getDetail(@PathVariable Long id) {
        OrderVO vo = orderService.getOrderDetail(id);
        return Result.ok(vo);
    }

    @Operation(summary = "取消订单（仅待付款）")
    @PutMapping("/{id}/cancel")
    public Result<String> cancel(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return Result.ok("已取消");
    }

    @Operation(summary = "卖家发货")
    @PutMapping("/{id}/ship")
    public Result<String> ship(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.shipOrder(userId, id);
        return Result.ok("已发货");
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{id}/confirm")
    public Result<String> confirm(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.confirmReceive(userId, id);
        return Result.ok("已确认收货");
    }

    @Operation(summary = "申请退款")
    @PutMapping("/{id}/refund")
    public Result<String> requestRefund(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.requestRefund(userId, id);
        return Result.ok("退款申请已提交");
    }

    @Operation(summary = "卖家同意退款")
    @PutMapping("/{id}/approve-refund")
    public Result<String> approveRefund(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.approveRefund(userId, id);
        return Result.ok("已同意退款");
    }

    @Operation(summary = "我买到的（买家视角）")
    @GetMapping("/bought")
    public Result<PageResult<OrderVO>> getBought(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResult<OrderVO> result = orderService.getBuyerOrders(userId, page, size, status);
        return Result.ok(result);
    }

    @Operation(summary = "我卖出的（卖家视角）")
    @GetMapping("/sold")
    public Result<PageResult<OrderVO>> getSold(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        PageResult<OrderVO> result = orderService.getSellerOrders(userId, page, size, status);
        return Result.ok(result);
    }
}
