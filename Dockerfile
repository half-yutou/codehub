# 使用官方 OpenJDK 运行时基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /codehub

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod

# 复制 Spring Boot 可执行 JAR 文件到容器
COPY target/codehub-0.0.1-SNAPSHOT.jar codehub.jar

# 暴露应用运行端口
EXPOSE 8848

# 启动命令
ENTRYPOINT ["java", "-jar", "codehub.jar"]
