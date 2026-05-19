# 部署指南

## 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

## 本地开发

### 1. 创建数据库

```bash
mysql -u root -p
CREATE DATABASE stone_reading DEFAULT CHARACTER SET utf8mb4;
USE stone_reading;
source backend/src/main/resources/db/init.sql;
```

### 2. 启动Redis

```bash
redis-server
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

## Docker部署

### 1. 构建镜像

```bash
cd backend
mvn clean package -DskipTests
cd ..

docker-compose -f deploy/docker-compose.yml up -d
```

### 2. 访问服务

- 前端: http://localhost:5173
- 后端: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html

## 生产部署

### 1. 配置环境变量

```bash
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export REDIS_HOST=your_redis_host
export REDIS_PASSWORD=your_redis_password
```

### 2. 启动服务

```bash
# 编译
cd backend
mvn clean package -DskipTests

# 启动后端
java -jar target/stone-reading-backend-1.0.0.jar --spring.profiles.active=prod

# 构建前端
cd ../frontend
npm run build
```

### 3. Nginx配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 健康检查

```bash
# 后端健康检查
curl http://localhost:8080/api/user/info

# 前端健康检查
curl http://localhost:5173
```