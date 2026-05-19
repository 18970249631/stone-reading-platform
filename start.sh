#!/bin/bash

echo "启动砾石阅读平台..."

# 检查 Java 版本
if ! java -version 2>&1 | grep -q "17"; then
    echo "请安装 JDK 17+"
    exit 1
fi

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "请安装 Maven"
    exit 1
fi

# 编译后端
echo "编译后端..."
cd backend
mvn clean package -DskipTests
cd ..

# 启动后端
echo "启动后端服务..."
nohup java -jar backend/target/stone-reading-backend-1.0.0.jar > logs/backend.log 2>&1 &
echo "后端服务已启动"

# 启动前端
echo "启动前端服务..."
cd frontend
npm install
nohup npm run dev > ../logs/frontend.log 2>&1 &
echo "前端服务已启动"

echo "平台启动完成！"
echo "前端地址: http://localhost:5173"
echo "后端地址: http://localhost:8080/api"