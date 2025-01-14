#!/bin/bash

# 配置区
JAR_NAME="codehub-0.0.1-SNAPSHOT.jar"                   # JAR 文件名
LOG_FILE="/var/www/project-java/logs/codehub.log"       # 日志文件路径
PROJECT_PATH="/var/www/project-java/codehub"            # 项目路径

# 进入项目目录
cd "$PROJECT_PATH" || {
    echo "项目路径不存在: $PROJECT_PATH"
    exit 1
}

# 清理并打包项目
echo "正在清理并打包项目..."
mvn clean package -DskipTests || {
    echo "Maven 打包失败！"
    exit 1
}
echo "Maven 打包成功！"

# 找到最新生成的 JAR 文件
NEW_JAR=$(find target -name "$JAR_NAME" 2>/dev/null)
if [ -z "$NEW_JAR" ]; then
    echo "未找到生成的 JAR 文件！"
    exit 1
fi
echo "找到 JAR 文件: $NEW_JAR"

# 检查是否已有相同进程在运行
PID=$(ps -ef | grep "$JAR_NAME" | grep -v "grep" | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "正在停止已有的进程: $PID"
    kill -9 "$PID"
    echo "进程已停止！"
fi

# 启动新的 JAR 包
echo "正在启动 JAR 包..."
nohup java -jar "$NEW_JAR" --spring.profiles.active=prod > "$LOG_FILE" 2>&1 &
NEW_PID=$!
echo "JAR 包启动成功，进程 PID: $NEW_PID"

# 输出日志路径
echo "日志路径: $LOG_FILE"
