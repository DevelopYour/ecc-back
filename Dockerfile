# 단일 스테이지: 실행만
FROM openjdk:17-jdk-slim

WORKDIR /app

# GitHub Actions에서 이미 빌드된 JAR 파일을 단순 복사
COPY build/libs/*.jar app.jar

# JVM 메모리 제한 설정
ENV JAVA_OPTS="-Xmx350m -Xms150m"

EXPOSE 8080

# 환경변수를 사용하여 JVM 옵션 적용
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]