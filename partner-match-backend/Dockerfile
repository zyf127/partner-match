# Docker 镜像构建
FROM openjdk:8-jre-slim

# 设定时区
ENV TZ=Asia/Shanghai

# Copy local code to the container image.
WORKDIR /app
COPY partner-match-backend-0.0.1-SNAPSHOT.jar ./partner-match-backend-0.0.1-SNAPSHOT.jar

# 入口
ENTRYPOINT ["java", "-jar", "/app/partner-match-backend-0.0.1-SNAPSHOT.jar"]
CMD ["--spring.profiles.active=prod"]
