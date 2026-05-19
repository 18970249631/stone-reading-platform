@echo off
echo 启动砾石阅读平台...

:: 检查Java版本
java -version 2>&1 | findstr /i "version"
if %errorlevel% neq 0 (
    echo 请安装 JDK 17+
    pause
    exit /b 1
)

:: 编译后端
echo 编译后端...
cd backend
mvn clean package -DskipTests
cd ..

:: 启动后端
echo 启动后端服务...
start "后端服务" java -jar backend\target\stone-reading-backend-1.0.0.jar

:: 启动前端
echo 启动前端服务...
cd frontend
npm install
start "前端服务" npm run dev

echo 平台启动完成！
echo 前端地址: http://localhost:5173
echo 后端地址: http://localhost:8080/api
pause