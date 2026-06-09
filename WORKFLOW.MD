	# 二手物品交易平台 — 后端开发工作流

## 一、项目概述

构建一个二手物品交易平台的后端服务，提供用户管理、商品发布与浏览、订单交易、即时通讯、评价系统等核心功能。

---

## 二、技术选型建议

| 层次 | 技术 | 选型理由 |
|------|------|----------|
| 框架 | **Spring Boot 3.x** | 生态成熟、社区活跃、适合企业级项目 |
| 语言 | **Java 17+** | 与 Spring Boot 最佳配合，稳定可靠 |
| 数据库 | **MySQL 8.0** | 关系型数据，事务支持完善 |
| 缓存 | **Redis** | 会话管理、热点数据缓存、消息队列 |
| ORM | **MyBatis-Plus** | 简化 CRUD，灵活 SQL 编写 |
| 认证 | **Spring Security + JWT** | 无状态认证，适合前后端分离 |
| 文件存储 | **阿里云 OSS / MinIO** | 支持图片上传与托管 |
| API 文档 | **Knife4j (Swagger)** | 自动生成 API 文档，方便调试 |
| 构建工具 | **Maven** | Java 生态标准构建工具 |
| 消息队列 | **RabbitMQ**（可选） | 异步处理订单、通知等 |

---

## 三、架构设计

```
┌─────────────────────────────────────────────────┐
│                  Nginx (反向代理)                 │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              Spring Boot 后端服务                 │
│                                                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │
│  │ Controller│ │ Service  │ │   Repository     │ │
│  │  (接口层) │ │ (业务层) │ │   (数据访问层)    │ │
│  └──────────┘ └──────────┘ └──────────────────┘ │
│                                                  │
│  ┌──────────────────────────────────────────────┐│
│  │  Spring Security + JWT 认证拦截器             ││
│  ├──────────────────────────────────────────────┤│
│  │  全局异常处理器 / 统一响应封装 / 参数校验      ││
│  └──────────────────────────────────────────────┘│
└────────────┬──────────────────┬─────────────────┘
             │                  │
    ┌────────▼──────┐   ┌──────▼────────┐
    │   MySQL        │   │    Redis       │
    │  (持久化存储)   │   │  (缓存/会话)   │
    └───────────────┘   └───────────────┘
```

---

## 四、功能模块划分

### 模块 1：用户服务 (user-service)

- [ ] 用户注册（手机号/邮箱 + 验证码）
- [ ] 用户登录（JWT Token 签发）
- [ ] 个人信息管理（头像、昵称、简介、地址）
- [ ] 实名认证（可选）
- [ ] 修改密码 / 找回密码

### 模块 2：商品服务 (product-service)

- [ ] 商品发布（标题、描述、价格、分类、成色、图片上传）
- [ ] 商品编辑与下架
- [ ] 商品列表（分页、排序）
- [ ] 商品搜索（关键词、分类、价格区间、成色、地区）
- [ ] 商品详情
- [ ] 商品收藏 / 取消收藏

### 模块 3：订单服务 (order-service)

- [ ] 创建订单（下单）
- [ ] 订单状态流转（待付款 → 已付款 → 已发货 → 已收货 → 已完成）
- [ ] 取消订单
- [ ] 申请退款/退货
- [ ] 订单列表（买家视角 / 卖家视角）
- [ ] 订单详情

### 模块 4：交易服务 (trade-service)

- [ ] 支付对接（支付宝 / 微信支付）
- [ ] 支付回调处理
- [ ] 交易流水记录

### 模块 5：消息服务 (message-service)

- [ ] 买家卖家在线聊天（WebSocket）
- [ ] 系统通知（订单状态变更、商品被收藏等）
- [ ] 消息历史记录

### 模块 6：评价服务 (review-service)

- [ ] 交易完成后互评
- [ ] 评分（1-5 星）
- [ ] 评价内容（文字 + 图片）
- [ ] 用户信誉分计算

### 模块 7：管理后台 (admin-service)

- [ ] 用户管理（封禁/解封）
- [ ] 商品审核与管理
- [ ] 数据统计看板（用户量、交易量、GMV）

---

## 五、数据库核心表设计（ER 概要）

```
users              用户表
  id, username, phone, email, password_hash, avatar, role,
  status, created_at, updated_at

categories          分类表
  id, name, parent_id, sort_order

products            商品表
  id, seller_id, category_id, title, description, price,
  original_price, condition(成色), status(在售/已售/下架),
  view_count, location, created_at, updated_at

product_images      商品图片表
  id, product_id, url, sort_order

favorites           收藏表
  id, user_id, product_id, created_at

orders              订单表
  id, buyer_id, seller_id, product_id, amount, status,
  payment_method, paid_at, shipped_at, received_at,
  completed_at, created_at

order_logs          订单日志表
  id, order_id, action, operator_id, remark, created_at

reviews             评价表
  id, order_id, reviewer_id, reviewee_id, rating,
  content, created_at

messages            消息表
  id, from_user_id, to_user_id, content, is_read,
  conversation_id, created_at

notifications       通知表
  id, user_id, type, title, content, is_read, created_at
```

---

## 六、实施阶段

### 阶段一：项目骨架搭建（预计 1-2 天）

1. 创建 Spring Boot 项目，配置 Maven 多模块结构
2. 配置基础依赖（Spring Web, Security, MyBatis-Plus, Redis, MySQL）
3. 搭建统一响应体 `Result<T>` 和全局异常处理
4. 配置 Swagger/Knife4j 接口文档
5. 编写项目配置文件（application.yml 多环境配置）
6. 设计并创建数据库表结构（DDL 脚本）

### 阶段二：用户模块（预计 2-3 天）

1. 实现用户注册（含验证码发送与校验）
2. 实现 JWT 登录认证
3. 实现 Spring Security 过滤器链
4. 实现个人信息 CRUD
5. 实现密码修改与找回

### 阶段三：商品模块（预计 2-3 天）

1. 实现商品发布（含多图片上传至 OSS）
2. 实现商品列表多条件分页查询
3. 实现商品搜索（Elasticsearch 可选，前期先用 MySQL LIKE）
4. 实现商品编辑、下架
5. 实现收藏功能

### 阶段四：订单模块（预计 2-3 天）

1. 实现订单创建（库存扣减、状态机）
2. 实现订单状态流转
3. 实现订单取消与退款申请
4. 实现订单历史记录

### 阶段五：交易与支付（预计 2 天）

1. 对接支付宝沙箱 / 微信支付
2. 实现支付回调与订单状态更新
3. 实现交易流水记录

### 阶段六：消息与评价（预计 2 天）

1. 实现 WebSocket 在线聊天
2. 实现系统通知
3. 实现交易评价与信誉分计算

### 阶段七：管理后台（预计 1-2 天）

1. 实现用户管理接口
2. 实现商品审核接口
3. 实现数据统计接口

### 阶段八：测试与优化（预计 2-3 天）

1. 单元测试覆盖核心业务
2. 接口集成测试
3. 性能优化（缓存策略、SQL 优化）
4. 安全加固（XSS 防护、接口限流、敏感数据脱敏）

---

## 七、项目结构

```
second-hand-market/
├── market-common/          # 公共模块（工具类、异常、统一响应）
├── market-auth/            # 认证模块（登录、注册、安全配置）
├── market-user/            # 用户模块
├── market-product/         # 商品模块
├── market-order/           # 订单模块
├── market-payment/         # 支付模块
├── market-message/         # 消息模块
├── market-review/          # 评价模块
├── market-admin/           # 管理后台模块
├── sql/                    # 数据库脚本
├── docs/                   # 文档
└── pom.xml                 # 父 POM
```

---

## 八、API 设计规范

- RESTful 风格，URL 使用名词复数形式
- 统一响应格式：`{ "code": 200, "message": "success", "data": {} }`
- 分页响应：`{ "code": 200, "data": { "records": [], "total": 100, "page": 1, "size": 10 } }`
- 认证方式：`Authorization: Bearer <jwt_token>`
- 版本控制：`/api/v1/...`

---

## 九、待确认事项

1. **技术栈确认**：上述选型是否合适？还是更倾向于 Node.js / Go / Python？
2. **部署方式**：单体部署还是微服务？（前期建议单体，后期可拆分）
3. **前端配合**：是否有前端团队？需要确认前后端接口联调方式
4. **支付接入**：是否需要真实支付对接，还是模拟支付？
5. **即时通讯**：是否需要完整的 IM 系统，还是简化的站内消息即可？
6. **是否需要 Elasticsearch**：商品搜索用 MySQL LIKE 先行，后期再引入 ES？

---

> **下一步**：请审核以上计划，确认技术选型和功能范围后，我将按阶段开始编码实现。
