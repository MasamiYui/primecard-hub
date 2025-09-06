-- ============================================================
-- PrimeCardHub 最终 MySQL DDL
-- 版本: 1.0.0
-- 创建时间: 2024-12-28
-- 说明: 整合所有表结构的最终DDL文件
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS primecardhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE primecardhub;

-- ============================================================
-- 管理端用户认证体系
-- ============================================================

-- 管理端用户表
CREATE TABLE admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(500) COMMENT '头像URL',
    role ENUM('SUPER_ADMIN', 'ADMIN', 'EDITOR', 'VIEWER') NOT NULL DEFAULT 'VIEWER' COMMENT '角色',
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    last_login_ip VARCHAR(45) COMMENT '最后登录IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理端用户表';

-- 管理端会话表
CREATE TABLE admin_user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE COMMENT 'JWT Token',
    refresh_token VARCHAR(500) COMMENT '刷新Token',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES admin_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理端会话表';

-- ============================================================
-- C端用户认证体系
-- ============================================================

-- C端用户表
CREATE TABLE client_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    union_id VARCHAR(100) UNIQUE COMMENT '微信UnionID',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender ENUM('MALE', 'FEMALE', 'UNKNOWN') DEFAULT 'UNKNOWN' COMMENT '性别',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    is_verified BOOLEAN DEFAULT FALSE COMMENT '是否验证',
    preferences JSON COMMENT '用户偏好设置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='C端用户表';

-- 第三方账号绑定表
CREATE TABLE client_user_oauth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_user_id BIGINT NOT NULL,
    provider ENUM('WECHAT', 'QQ', 'ALIPAY', 'WEIBO') NOT NULL COMMENT '第三方平台',
    provider_user_id VARCHAR(100) NOT NULL COMMENT '第三方平台用户ID',
    union_id VARCHAR(100) COMMENT '第三方平台UnionID',
    access_token TEXT COMMENT '访问令牌',
    raw_user_info JSON COMMENT '原始用户信息',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE' COMMENT '绑定状态',
    bound_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    FOREIGN KEY (client_user_id) REFERENCES client_users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_provider_user (provider, provider_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方账号绑定表';

-- 手机验证码表
CREATE TABLE sms_verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type ENUM('LOGIN', 'REGISTER', 'BIND_PHONE', 'RESET_PASSWORD') NOT NULL COMMENT '验证码类型',
    is_used BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45) COMMENT '请求IP'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='手机验证码表';

-- C端用户会话表
CREATE TABLE client_user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_user_id BIGINT NOT NULL,
    session_token VARCHAR(500) NOT NULL UNIQUE COMMENT '会话令牌',
    login_type ENUM('WECHAT', 'PHONE', 'EMAIL') NOT NULL COMMENT '登录类型',
    device_type ENUM('WECHAT_MINI', 'H5', 'APP', 'WEB') COMMENT '设备类型',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_user_id) REFERENCES client_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='C端用户会话表';

-- ============================================================
-- 基础数据表
-- ============================================================

-- 分类表
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 标签表
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    color VARCHAR(20) COMMENT '标签颜色',
    usage_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================================
-- 业务数据表
-- ============================================================

-- 信用卡表
CREATE TABLE credit_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL COMMENT '银行名称',
    bank_logo VARCHAR(500) COMMENT '银行Logo',
    card_name VARCHAR(100) NOT NULL COMMENT '卡片名称',
    card_type ENUM('CREDIT', 'DEBIT', 'PREPAID') NOT NULL COMMENT '卡片类型',
    card_level ENUM('STANDARD', 'GOLD', 'PLATINUM', 'DIAMOND') NOT NULL COMMENT '卡片级别',
    annual_fee VARCHAR(50) COMMENT '年费',
    apply_url VARCHAR(500) COMMENT '申请链接',
    card_image VARCHAR(500) COMMENT '卡片图片',
    description TEXT COMMENT '卡片描述',
    cashback_rate DECIMAL(5,2) COMMENT '返现比例',
    points_ratio INT COMMENT '积分比例',
    interest_rate DECIMAL(5,2) COMMENT '利率',
    apply_count INT NOT NULL DEFAULT 0 COMMENT '申请人数',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    is_recommended BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否推荐',
    is_hot BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否热门',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    publish_time DATETIME NULL COMMENT '发布时间',
    created_by BIGINT COMMENT '创建人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES admin_users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信用卡表';

-- 信用卡权益表
CREATE TABLE card_benefits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL COMMENT '信用卡ID',
    benefit VARCHAR(255) NOT NULL COMMENT '权益描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_id) REFERENCES credit_cards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信用卡权益表';

-- 资讯表
CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content LONGTEXT NOT NULL COMMENT '内容',
    summary TEXT NOT NULL COMMENT '摘要',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    cover_image VARCHAR(500) COMMENT '封面图片',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    is_recommended BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否推荐',
    is_hot BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否热门',
    is_top BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
    publish_time DATETIME NULL COMMENT '发布时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (author_id) REFERENCES admin_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯表';

-- 资讯标签关联表
CREATE TABLE news_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    news_id BIGINT NOT NULL COMMENT '资讯ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE KEY uk_news_tag (news_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯标签关联表';

-- 轮播图表
CREATE TABLE banners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    link_type ENUM('NONE', 'CARD', 'NEWS', 'EXTERNAL', 'MINIPROGRAM') NOT NULL DEFAULT 'NONE' COMMENT '链接类型',
    link_url VARCHAR(500) COMMENT '链接地址',
    link_id BIGINT COMMENT '关联ID（当link_type为CARD或NEWS时使用）',
    link_appid VARCHAR(100) COMMENT '小程序AppID（当link_type为MINIPROGRAM时使用）',
    link_page VARCHAR(200) COMMENT '小程序页面路径（当link_type为MINIPROGRAM时使用）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（数字越小越靠前）',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    start_time DATETIME COMMENT '展示开始时间（可选）',
    end_time DATETIME COMMENT '展示结束时间（可选）',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    click_count INT NOT NULL DEFAULT 0 COMMENT '点击量',
    created_by BIGINT COMMENT '创建人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES admin_users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- ============================================================
-- C端用户业务数据表
-- ============================================================

-- 用户信用卡表
CREATE TABLE client_user_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_user_id BIGINT NOT NULL COMMENT 'C端用户ID',
    credit_card_id BIGINT NOT NULL COMMENT '信用卡ID',
    card_number_encrypted VARCHAR(255) COMMENT '加密卡号',
    card_holder VARCHAR(100) COMMENT '持卡人',
    expiry_date VARCHAR(10) COMMENT '到期日期',
    credit_limit DECIMAL(10,2) COMMENT '信用额度',
    status ENUM('ACTIVE', 'INACTIVE', 'EXPIRED') DEFAULT 'ACTIVE' COMMENT '状态',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '是否主卡',
    nickname VARCHAR(50) COMMENT '卡片昵称',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_user_id) REFERENCES client_users(id) ON DELETE CASCADE,
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信用卡表';

-- 用户资讯互动表
CREATE TABLE client_user_news_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_user_id BIGINT NOT NULL COMMENT 'C端用户ID',
    news_id BIGINT NOT NULL COMMENT '资讯ID',
    interaction_type ENUM('LIKE', 'FAVORITE', 'SHARE') NOT NULL COMMENT '互动类型',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_user_id) REFERENCES client_users(id) ON DELETE CASCADE,
    FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_news_interaction (client_user_id, news_id, interaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资讯互动表';

-- ============================================================
-- 索引优化
-- ============================================================

-- 用户相关索引
CREATE INDEX idx_client_users_union_id ON client_users(union_id);
CREATE INDEX idx_client_users_phone ON client_users(phone);
CREATE INDEX idx_sms_codes_phone_type ON sms_verification_codes(phone, type, created_at);

-- 业务数据索引
CREATE INDEX idx_credit_cards_status ON credit_cards(status, is_recommended, is_hot);
CREATE INDEX idx_news_status ON news(status, publish_time, is_recommended, is_hot, is_top);
CREATE INDEX idx_banners_active ON banners(status, start_time, end_time, sort_order);
CREATE INDEX idx_categories_status ON categories(status, sort_order);

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员用户
INSERT INTO admin_users (username, email, password, real_name, role, status) VALUES
('admin', 'admin@primehub.com', '$2a$10$eDIJO.xBXAYYi/Qg.1O5A.0BQgTTBw.QiRQY1GkfG/uFj0C.f6QXC', '系统管理员', 'SUPER_ADMIN', 'ACTIVE'),
('editor', 'editor@primehub.com', '$2a$10$eDIJO.xBXAYYi/Qg.1O5A.0BQgTTBw.QiRQY1GkfG/uFj0C.f6QXC', '内容编辑', 'EDITOR', 'ACTIVE');

-- 分类数据
INSERT INTO categories (name, description, icon, sort_order, status) VALUES
('信用卡资讯', '最新信用卡相关新闻和资讯', 'news-icon', 1, 'ACTIVE'),
('用卡技巧', '信用卡使用技巧和攻略', 'tips-icon', 2, 'ACTIVE'),
('优惠活动', '信用卡最新优惠活动信息', 'discount-icon', 3, 'ACTIVE'),
('积分攻略', '信用卡积分获取和使用攻略', 'points-icon', 4, 'ACTIVE'),
('理财知识', '个人理财和信用卡相关知识', 'finance-icon', 5, 'ACTIVE');

-- 标签数据
INSERT INTO tags (name, color, usage_count) VALUES
('信用卡', '#FF5733', 0), ('优惠', '#33FF57', 0), ('积分', '#3357FF', 0),
('旅行', '#F033FF', 0), ('美食', '#FF3333', 0), ('购物', '#33FFF3', 0),
('理财', '#FFFF33', 0), ('新手', '#8333FF', 0), ('高端卡', '#FF8333', 0), ('活动', '#33FFBE', 0);

-- ============================================================
-- DDL 完成
-- ============================================================