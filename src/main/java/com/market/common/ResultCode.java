package com.market.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "服务器内部错误"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    USER_PHONE_EXISTS(1004, "手机号已注册"),
    USER_EMAIL_EXISTS(1005, "邮箱已注册"),
    USER_NOT_LOGIN(1006, "用户未登录"),

    // 商品相关 2xxx
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OFF_SHELF(2002, "商品已下架"),
    PRODUCT_SOLD(2003, "商品已售出"),
    PRODUCT_NOT_OWNER(2004, "不是您的商品"),

    // 订单相关 3xxx
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),
    ORDER_CANNOT_CANCEL(3003, "订单无法取消"),
    ORDER_NOT_YOURS(3004, "不是您的订单"),

    // 支付相关 4xxx
    PAYMENT_FAILED(4001, "支付失败"),
    PAYMENT_ALREADY_PAID(4002, "订单已支付"),

    // 评价相关 5xxx
    REVIEW_ALREADY_EXISTS(5001, "已经评价过了"),
    REVIEW_NOT_ALLOWED(5002, "暂不能评价"),

    // 文件相关 6xxx
    FILE_UPLOAD_FAILED(6001, "文件上传失败"),
    FILE_FORMAT_ERROR(6002, "文件格式不支持"),
    FILE_TOO_LARGE(6003, "文件过大");

    private final int code;
    private final String message;
}
