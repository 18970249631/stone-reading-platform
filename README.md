# 📚 砾石阅读平台

基于 Vue 3 + Spring Boot 的全栈阅读平台，支持免费+付费混合变现体系、智能搜索、AI内容审核等功能。

## 🌟 功能特性

### Phase 1 - 核心阅读
- ✅ 用户注册/登录 (JWT认证)
- ✅ 书籍浏览/搜索
- ✅ 阅读器 (仿真翻页/滑动/主题切换)
- ✅ 书架管理
- ✅ 文档上传解析 (PDF/DOCX)
- ✅ 基础后台管理

### Phase 2 - 交易区系统
- ✅ 脚本交易功能
- ✅ 书籍/出版商交易
- ✅ 订单管理
- ✅ 支付系统对接

### Phase 3 - 专业功能
- ✅ 作家认证与后台
- ✅ AI内容审核系统
- ✅ 智能搜索 (Elasticsearch)
- ✅ 数据运营大屏

### Phase 4 - 部署优化
- ✅ Docker Compose 部署
- ✅ GitHub Actions CI/CD
- ✅ GitHub Pages 静态部署

## 🛠️ 技术栈

### 前端
- **Vue 3** + **Vite** + **Element Plus**
- **Pinia** 状态管理
- **Vue Router** 路由
- **Sass** 样式

### 后端
- **Spring Boot 3.2**
- **Spring Security** + **JWT** 认证
- **MyBatis-Plus** ORM
- **MySQL 8.0** + **Redis 6.0**
- **Elasticsearch** 全文搜索

### 部署
- **Docker Compose** 容器化
- **GitHub Actions** CI/CD
- **GitHub Pages** 静态站点

## 📁 项目结构

```
stone-reading-platform/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/stonereading/
│   │   ├── controller/        # REST API 控制器
│   │   ├── service/           # 业务服务层
│   │   ├── mapper/            # MyBatis 数据访问
│   │   ├── entity/            # 数据库实体
│   │   ├── dto/               # 数据传输对象
│   │   ├── config/            # Spring 配置类
│   │   ├── filter/            # 安全过滤器
│   │   ├── exception/         # 全局异常处理
│   │   └── util/              # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml    # 应用配置
│   │   └── db/init.sql        # 数据库初始化脚本
│   └── pom.xml                # Maven 依赖
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── api/               # API 封装
│   │   └── styles/            # 全局样式
│   ├── index.html
│   ├── package.json
│   └── vite.config.js         # Vite 配置
├── deploy/                    # 部署配置
│   └── docker-compose.yml     # Docker Compose
├── docs/                      # 项目文档
└── .github/workflows/         # GitHub Actions
```

## 🚀 快速开始

### 环境要求
| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Maven | 3.8+ |

### 本地开发

**1. 启动后端**

```bash
cd backend
mvn spring-boot:run
```

后端服务：http://localhost:8080

**2. 启动前端**

```bash
cd frontend
npm install
npm run dev
```

前端页面：http://localhost:5173

### Docker 部署

```bash
cd deploy
docker-compose up -d
```

### 数据库初始化

创建数据库并导入初始数据：
```bash
mysql -u root -p -e "CREATE DATABASE stone_reading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p stone_reading < backend/src/main/resources/db/init.sql
```

## 🌐 访问地址

| 服务 | 地址 |
|------|------|
| 前端首页 | http://localhost:5173 |
| 管理后台 | http://localhost:5173/admin |
| 后端API | http://localhost:8080/api |
| Swagger文档 | http://localhost:8080/swagger-ui.html |

## 🚀 GitHub Pages 部署

项目已配置自动部署到 GitHub Pages：

```
https://18970249631.github.io/stone-reading-platform/
```

## 🔄 CI/CD 流程

每次推送到 `main` 分支时，GitHub Actions 自动执行：
1. ✅ 后端构建测试
2. ✅ 前端构建
3. ✅ 部署到 GitHub Pages

## 🔧 配置说明

### 后端配置
- `application.yml` - 数据库连接、JWT密钥等
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

### 前端配置
- `vite.config.js` - Vite构建配置
- `src/api/axios.js` - API请求配置

## 📋 API 接口文档

启动后访问 Swagger UI：
```
http://localhost:8080/swagger-ui.html
```

## 📜 License

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！