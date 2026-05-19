# 砾石阅读平台

基于 Vue 3 + Spring Boot 的全栈阅读平台，支持多端阅读、会员系统、书籍交易等功能。

## 技术栈

### 前端
- Vue 3 + Vite + Element Plus
- UniApp (移动端)
- Pinia 状态管理
- Vue Router

### 后端
- Spring Boot 3.2
- Spring Security + JWT
- MyBatis-Plus
- MySQL + Redis

## 项目结构

```
stone-reading-platform/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/stonereading/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── mapper/             # 数据访问
│   │   ├── entity/             # 实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   ├── filter/             # 过滤器
│   │   ├── exception/           # 异常处理
│   │   └── util/                # 工具类
│   └── src/main/resources/
│       ├── application.yml
│       └── db/init.sql         # 数据库初始化
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── components/        # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # 状态管理
│   │   ├── api/              # API 封装
│   │   └── styles/           # 全局样式
│   └── package.json
└── docs/                      # 文档
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 后端启动

1. 创建数据库并导入数据：
```bash
mysql -u root -p < backend/src/main/resources/db/init.sql
```

2. 修改 `backend/src/main/resources/application.yml` 中的数据库配置

3. 启动后端：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## 功能模块

### Phase 1 - MVP
- [x] 用户注册/登录 (JWT认证)
- [x] 书籍浏览/搜索
- [x] 阅读器 (仿真翻页/滑动)
- [x] 书架管理
- [x] 文档上传解析
- [x] 基础后台管理

### Phase 2 - 商业化
- [ ] 会员体系 (基础会员/无限卡)
- [ ] 支付系统 (微信/支付宝)
- [ ] 广告激励系统
- [ ] 书籍交易区

### Phase 3 - 专业功能
- [ ] 作家后台
- [ ] 剧本交易区
- [ ] 出版商交易区
- [ ] AI内容审核

### Phase 4 - 生态完善
- [ ] 音视频上传
- [ ] 区域分管理员
- [ ] 社区功能
- [ ] AI推荐

## API 文档

启动后访问：http://localhost:8080/api/swagger-ui.html

## License

MIT