# PrimeCardHub 数据库DDL说明

## 文件说明

### mysql-final-ddl.sql
**最终的MySQL数据库DDL文件，包含完整的表结构设计。**

#### 主要特性：
- ✅ C端和管理端用户分离设计
- ✅ 支持微信登录、手机验证码登录
- ✅ 完整的业务表结构（信用卡、资讯、轮播图等）
- ✅ 性能优化索引
- ✅ 初始化数据

#### 表结构概览：

**用户认证体系：**
- `admin_users` - 管理端用户表
- `admin_user_sessions` - 管理端会话表
- `client_users` - C端用户表
- `client_user_oauth` - 第三方账号绑定表
- `sms_verification_codes` - 手机验证码表
- `client_user_sessions` - C端用户会话表

**业务数据表：**
- `categories` - 分类表
- `tags` - 标签表
- `credit_cards` - 信用卡表
- `card_benefits` - 信用卡权益表
- `news` - 资讯表
- `news_tags` - 资讯标签关联表
- `banners` - 轮播图表

**C端用户业务表：**
- `client_user_cards` - 用户信用卡表
- `client_user_news_interactions` - 用户资讯互动表

### test-data.sql
测试数据文件，包含示例数据用于开发和测试。

## 使用说明

### 1. 准备MySQL环境
```bash
# 安装MySQL 8.0+
# 创建数据库用户（可选）
CREATE USER 'primecard'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON primecarddb.* TO 'primecard'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 执行DDL
```bash
# 方式1：MySQL命令行
mysql -u root -p < mysql-final-ddl.sql

# 方式2：MySQL客户端
# 连接到MySQL后执行
source /path/to/mysql-final-ddl.sql;

# 方式3：使用可视化工具
# 如Navicat、DBeaver等，直接导入执行
```

### 3. 导入测试数据（可选）
```bash
# 在执行完DDL后导入测试数据
mysql -u root -p primecarddb < test-data.sql
```

### 4. 配置应用
确保 `application.yml` 中的数据库配置正确：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/primecarddb?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driverClassName: com.mysql.cj.jdbc.Driver
```

## 注意事项

1. **字符集**：所有表使用 `utf8mb4` 字符集，支持完整的UTF-8字符（包括emoji）
2. **密码安全**：示例用户密码已使用BCrypt加密，原始密码为 `password`
3. **外键约束**：已设置适当的外键约束，确保数据完整性
4. **索引优化**：已创建常用查询的索引，提升查询性能
5. **时区设置**：建议使用 `Asia/Shanghai` 时区

## 登录信息

### 管理端默认账号
- **超级管理员**：admin / password
- **编辑员**：editor / password

### 开发调试
如需使用H2数据库进行开发调试，请：
1. 注释掉MySQL配置
2. 启用H2配置
3. 使用H2兼容的DDL语法

## 版本历史
- v1.0.0 - 初始版本，完整的表结构设计
- 支持C端和管理端用户分离
- 支持多种登录方式
- 包含完整的业务功能表