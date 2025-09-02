# PrimeCard Hub Backend

## 项目简介

PrimeCard Hub 后端服务，基于 Spring Boot 构建的信用卡管理平台后端 API。

## 技术栈

- **框架**: Spring Boot 2.7.x
- **ORM**: Spring Data JPA
- **安全框架**: Spring Security
- **认证方式**: JWT
- **数据库**: H2（开发）/ MySQL（生产）
- **API文档**: Springdoc OpenAPI
- **构建工具**: Maven

## 快速开始

### 环境要求

- Java 17 或更高版本
- 无需安装 Maven（项目已集成 Maven Wrapper）

### 使用 Maven Wrapper 构建项目

本项目已集成 Maven Wrapper，无需在本地安装 Maven 即可构建和运行项目。

#### 在 Unix/Linux/macOS 系统上：

```bash
# 编译项目
./mvnw compile

# 运行测试
./mvnw test

# 打包项目
./mvnw package

# 运行应用
./mvnw spring-boot:run

# 清理项目
./mvnw clean
```

#### 在 Windows 系统上：

```cmd
# 编译项目
mvnw.cmd compile

# 运行测试
mvnw.cmd test

# 打包项目
mvnw.cmd package

# 运行应用
mvnw.cmd spring-boot:run

# 清理项目
mvnw.cmd clean
```

### 开发环境配置

1. 克隆项目到本地
2. 进入 backend 目录
3. 运行 `./mvnw spring-boot:run` 启动开发服务器
4. 访问 http://localhost:8080 查看应用

### API 文档

启动应用后，可以通过以下地址访问 API 文档：
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### 数据库

开发环境使用 H2 内存数据库，数据库文件位于 `data/` 目录下。

### 测试数据

项目提供了测试数据导入脚本，运行以下命令导入测试数据：

```bash
./import-test-data.sh
```

## 项目结构

```
backend/
├── .mvn/                    # Maven Wrapper 配置
├── src/
│   ├── main/
│   │   ├── java/           # Java 源代码
│   │   └── resources/      # 资源文件
│   └── test/               # 测试代码
├── data/                   # 数据库文件
├── mvnw                    # Maven Wrapper 脚本 (Unix/Linux/macOS)
├── mvnw.cmd               # Maven Wrapper 脚本 (Windows)
├── pom.xml                # Maven 项目配置
└── README.md              # 项目说明文档
```

## 常用命令

```bash
# 查看 Maven 版本
./mvnw --version

# 查看项目依赖
./mvnw dependency:tree

# 运行特定测试
./mvnw test -Dtest=TestClassName

# 跳过测试打包
./mvnw package -DskipTests

# 生成测试报告
./mvnw surefire-report:report
```

## 贡献指南

请参考项目根目录下的编码规范文档，确保代码质量和一致性。

## 许可证

[添加许可证信息]