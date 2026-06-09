-- =============================================
-- 二手物品交易平台 — 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS second_hand_market
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE second_hand_market;

-- =============================================
-- 用户表
-- =============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(64)     NOT NULL                 COMMENT '用户名',
    `phone`         VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    `email`         VARCHAR(128)    DEFAULT NULL             COMMENT '邮箱',
    `password_hash` VARCHAR(256)    NOT NULL                 COMMENT '密码哈希',
    `avatar`        VARCHAR(512)    DEFAULT NULL             COMMENT '头像URL',
    `nickname`      VARCHAR(64)     DEFAULT NULL             COMMENT '昵称',
    `bio`           VARCHAR(512)    DEFAULT NULL             COMMENT '个人简介',
    `role`          VARCHAR(32)     NOT NULL DEFAULT 'USER'  COMMENT '角色: USER / ADMIN',
    `credit_score`  INT             NOT NULL DEFAULT 100     COMMENT '信誉分',
    `status`        TINYINT         NOT NULL DEFAULT 0       COMMENT '状态: 0正常 1禁用',
    `last_login_at` DATETIME        DEFAULT NULL             COMMENT '最后登录时间',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除: 0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    INDEX `idx_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 分类表
-- =============================================
CREATE TABLE IF NOT EXISTS `category` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`          VARCHAR(64)     NOT NULL                 COMMENT '分类名称',
    `parent_id`     BIGINT          NOT NULL DEFAULT 0       COMMENT '父分类ID, 0为顶级分类',
    `icon`          VARCHAR(256)    DEFAULT NULL             COMMENT '分类图标',
    `sort_order`    INT             NOT NULL DEFAULT 0       COMMENT '排序',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 0禁用 1启用',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =============================================
-- 商品表
-- =============================================
CREATE TABLE IF NOT EXISTS `product` (
    `id`             BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
    `seller_id`      BIGINT          NOT NULL                 COMMENT '卖家ID',
    `category_id`    BIGINT          NOT NULL                 COMMENT '分类ID',
    `title`          VARCHAR(256)    NOT NULL                 COMMENT '商品标题',
    `description`    TEXT            DEFAULT NULL             COMMENT '商品描述',
    `price`          DECIMAL(10,2)   NOT NULL                 COMMENT '售价',
    `original_price` DECIMAL(10,2)   DEFAULT NULL             COMMENT '原价',
    `condition`      TINYINT         NOT NULL DEFAULT 1       COMMENT '成色: 1全新 2几乎全新 3轻微使用 4明显使用',
    `status`         TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1在售 2已售 3已下架',
    `view_count`     INT             NOT NULL DEFAULT 0       COMMENT '浏览次数',
    `favorite_count` INT             NOT NULL DEFAULT 0       COMMENT '收藏次数',
    `location`       VARCHAR(128)    DEFAULT NULL             COMMENT '所在地',
    `created_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_price` (`price`),
    INDEX `idx_created_at` (`created_at`),
    FULLTEXT INDEX `ft_title_desc` (`title`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =============================================
-- 商品图片表
-- =============================================
CREATE TABLE IF NOT EXISTS `product_image` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '图片ID',
    `product_id`    BIGINT          NOT NULL                 COMMENT '商品ID',
    `url`           VARCHAR(512)    NOT NULL                 COMMENT '图片URL',
    `sort_order`    INT             NOT NULL DEFAULT 0       COMMENT '排序',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- =============================================
-- 收藏表
-- =============================================
CREATE TABLE IF NOT EXISTS `favorite` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `product_id`    BIGINT          NOT NULL                 COMMENT '商品ID',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =============================================
-- 订单表
-- =============================================
CREATE TABLE IF NOT EXISTS `order` (
    `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '订单ID',
    `order_no`        VARCHAR(32)     NOT NULL                 COMMENT '订单编号',
    `buyer_id`        BIGINT          NOT NULL                 COMMENT '买家ID',
    `seller_id`       BIGINT          NOT NULL                 COMMENT '卖家ID',
    `product_id`      BIGINT          NOT NULL                 COMMENT '商品ID',
    `amount`          DECIMAL(10,2)   NOT NULL                 COMMENT '订单金额',
    `status`          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1待付款 2已付款 3已发货 4已收货 5已完成 6已取消 7退款中 8已退款',
    `payment_method`  VARCHAR(32)     DEFAULT NULL             COMMENT '支付方式: ALIPAY / WECHAT',
    `remark`          VARCHAR(512)    DEFAULT NULL             COMMENT '买家备注',
    `paid_at`         DATETIME        DEFAULT NULL             COMMENT '支付时间',
    `shipped_at`      DATETIME        DEFAULT NULL             COMMENT '发货时间',
    `received_at`     DATETIME        DEFAULT NULL             COMMENT '收货时间',
    `completed_at`    DATETIME        DEFAULT NULL             COMMENT '完成时间',
    `canceled_at`     DATETIME        DEFAULT NULL             COMMENT '取消时间',
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    INDEX `idx_buyer_id` (`buyer_id`),
    INDEX `idx_seller_id` (`seller_id`),
    INDEX `idx_product_id` (`product_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =============================================
-- 订单日志表
-- =============================================
CREATE TABLE IF NOT EXISTS `order_log` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '日志ID',
    `order_id`      BIGINT          NOT NULL                 COMMENT '订单ID',
    `order_no`      VARCHAR(32)     NOT NULL                 COMMENT '订单编号',
    `action`        VARCHAR(64)     NOT NULL                 COMMENT '操作类型',
    `operator_id`   BIGINT          NOT NULL                 COMMENT '操作人ID',
    `remark`        VARCHAR(256)    DEFAULT NULL             COMMENT '备注',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单日志表';

-- =============================================
-- 评价表
-- =============================================
CREATE TABLE IF NOT EXISTS `review` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '评价ID',
    `order_id`      BIGINT          NOT NULL                 COMMENT '订单ID',
    `reviewer_id`   BIGINT          NOT NULL                 COMMENT '评价人ID',
    `reviewee_id`   BIGINT          NOT NULL                 COMMENT '被评价人ID',
    `rating`        TINYINT         NOT NULL                 COMMENT '评分: 1-5',
    `content`       VARCHAR(1024)   DEFAULT NULL             COMMENT '评价内容',
    `images`        VARCHAR(2048)   DEFAULT NULL             COMMENT '评价图片, 逗号分隔',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_reviewer` (`order_id`, `reviewer_id`),
    INDEX `idx_reviewee_id` (`reviewee_id`),
    INDEX `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- =============================================
-- 消息表
-- =============================================
CREATE TABLE IF NOT EXISTS `message` (
    `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '消息ID',
    `conversation_id` VARCHAR(64)     NOT NULL                 COMMENT '会话ID',
    `from_user_id`    BIGINT          NOT NULL                 COMMENT '发送者ID',
    `to_user_id`      BIGINT          NOT NULL                 COMMENT '接收者ID',
    `content`         TEXT            NOT NULL                 COMMENT '消息内容',
    `message_type`    TINYINT         NOT NULL DEFAULT 1       COMMENT '消息类型: 1文本 2图片 3系统消息',
    `is_read`         TINYINT         NOT NULL DEFAULT 0       COMMENT '是否已读: 0未读 1已读',
    `read_at`         DATETIME        DEFAULT NULL             COMMENT '阅读时间',
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    INDEX `idx_conversation_id` (`conversation_id`),
    INDEX `idx_from_user_id` (`from_user_id`),
    INDEX `idx_to_user_id` (`to_user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- =============================================
-- 通知表
-- =============================================
CREATE TABLE IF NOT EXISTS `notification` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '通知ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `type`          VARCHAR(32)     NOT NULL                 COMMENT '通知类型: ORDER / MESSAGE / SYSTEM / FAVORITE',
    `title`         VARCHAR(256)    NOT NULL                 COMMENT '通知标题',
    `content`       VARCHAR(1024)   DEFAULT NULL             COMMENT '通知内容',
    `ref_id`        VARCHAR(64)     DEFAULT NULL             COMMENT '关联业务ID',
    `is_read`       TINYINT         NOT NULL DEFAULT 0       COMMENT '是否已读: 0未读 1已读',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- =============================================
-- 支付流水表
-- =============================================
CREATE TABLE IF NOT EXISTS `payment_record` (
    `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '支付记录ID',
    `order_id`        BIGINT          NOT NULL                 COMMENT '订单ID',
    `order_no`        VARCHAR(32)     NOT NULL                 COMMENT '订单编号',
    `user_id`         BIGINT          NOT NULL                 COMMENT '付款人ID',
    `amount`          DECIMAL(10,2)   NOT NULL                 COMMENT '支付金额',
    `payment_method`  VARCHAR(32)     NOT NULL                 COMMENT '支付方式',
    `transaction_id`  VARCHAR(128)    DEFAULT NULL             COMMENT '第三方交易号',
    `status`          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态: 1待支付 2支付成功 3支付失败',
    `paid_at`         DATETIME        DEFAULT NULL             COMMENT '支付时间',
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付流水表';

-- =============================================
-- 初始化数据：分类
-- =============================================
INSERT INTO `category` (`name`, `parent_id`, `sort_order`) VALUES
('手机数码', 0, 1),
('电脑办公', 0, 2),
('家用电器', 0, 3),
('服饰鞋包', 0, 4),
('图书教材', 0, 5),
('运动户外', 0, 6),
('生活家居', 0, 7),
('其他', 0, 99);

INSERT INTO `category` (`name`, `parent_id`, `sort_order`) VALUES
('智能手机', 1, 1),
('平板电脑', 1, 2),
('智能手表', 1, 3),
('耳机音箱', 1, 4);

INSERT INTO `category` (`name`, `parent_id`, `sort_order`) VALUES
('笔记本电脑', 2, 1),
('台式机', 2, 2),
('显示器', 2, 3),
('键盘鼠标', 2, 4);

-- 创建默认管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `phone`, `password_hash`, `role`, `credit_score`, `status`) VALUES
('admin', '13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'ADMIN', 100, 0);
