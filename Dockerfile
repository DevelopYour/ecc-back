# Dockerfile
FROM openjdk:17-jdk-slim

# 작업 디렉토리
WORKDIR /app

# JAR 파일 복사
COPY build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]