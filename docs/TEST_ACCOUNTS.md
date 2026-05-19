# 📋 测试账号说明

## 🎯 快速开始

### 方法一：自行注册（推荐）

1. 访问前端页面
2. 点击「注册」按钮
3. 填写信息完成注册
4. 使用注册的账号登录

### 方法二：使用预设测试账号

#### 普通用户账号
| 用户名 | 密码 | 说明 |
|--------|------|------|
| `testuser` | `123456` | 普通用户账号 |
| `demo` | `demo123` | 演示账号 |

#### VIP 用户账号
| 用户名 | 密码 | VIP等级 |
|--------|------|---------|
| `vipuser` | `vip123` | 1 |
| `premium` | `premium` | 2 |

#### 管理员账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | ADMIN |
| `superadmin` | `super123` | ADMIN |

#### 作家账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| `author` | `author123` | AUTHOR |
| `writer` | `writer123` | AUTHOR |

---

## 🗄️ 数据库初始化

### MySQL 数据库

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS stone_reading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stone_reading;

-- 插入测试用户
INSERT INTO `user` (username, password, nickname, role, vip_level, status, created_at, updated_at) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 'USER', 0, 1, NOW(), NOW()),
('demo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '演示账号', 'USER', 0, 1, NOW(), NOW()),
('vipuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'VIP用户', 'USER', 1, 1, NOW(), NOW()),
('premium', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '高级VIP', 'USER', 2, 1, NOW(), NOW()),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'ADMIN', 0, 1, NOW(), NOW()),
('superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'ADMIN', 0, 1, NOW(), NOW()),
('author', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试作家', 'AUTHOR', 0, 1, NOW(), NOW()),
('writer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '作家账号', 'AUTHOR', 0, 1, NOW(), NOW());
```

**注意：** 以上密码是加密的 BCrypt 哈希值，明文密码都是 `123456`

---

## 🔐 密码说明

### 密码加密

所有测试账号的密码都是 `123456`，使用 BCrypt 加密

### 注册新用户

1. 访问首页
2. 点击「注册」
3. 填写用户名、密码、邮箱
4. 提交注册
5. 使用新账号登录

---

## 🚀 本地开发测试

### 启动服务

```bash
# 终端 1 - 后端
cd backend
mvn spring-boot:run

# 终端 2 - 前端
cd frontend
npm install
npm run dev
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 前端首页 | http://localhost:5173 |
| 管理后台 | http://localhost:5173/admin |
| 后端API | http://localhost:8080/api |
| Swagger文档 | http://localhost:8080/swagger-ui.html |

---

## 📱 功能测试清单

### 用户相关
- [ ] 新用户注册
- [ ] 用户登录
- [ ] 修改密码
- [ ] 更新个人信息
- [ ] 查看个人资料
- [ ] VIP充值

### 书籍相关
- [ ] 浏览书籍列表
- [ ] 搜索书籍
- [ ] 查看书籍详情
- [ ] 收藏书籍
- [ ] 阅读书籍
- [ ] 书架管理

### 交易相关
- [ ] 浏览商品
- [ ] 购买商品
- [ ] 查看订单
- [ ] 订单支付
- [ ] 退款申请

### 作家中心
- [ ] 作家认证
- [ ] 发布作品
- [ ] 编辑作品
- [ ] 查看收益
- [ ] 数据统计

### 管理后台
- [ ] 用户管理
- [ ] 书籍管理
- [ ] 订单管理
- [ ] 内容审核
- [ ] 数据大屏

---

## 🐛 常见问题

### 无法登录？
1. 检查后端服务是否启动
2. 检查数据库连接
3. 确认用户名密码正确
4. 查看浏览器控制台错误

### 注册失败？
1. 检查用户名是否已被占用
2. 检查邮箱格式是否正确
3. 密码长度至少6位

### 页面空白？
1. 检查前端服务是否启动
2. 检查浏览器控制台错误
3. 清除浏览器缓存

---

## 📚 更多文档

- [部署文档](./DEPLOYMENT.md)
- [项目README](../README.md)
