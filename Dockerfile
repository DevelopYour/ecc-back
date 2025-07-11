# 멀티스테이지 빌드 - 빌드 단계
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# Gradle 설정 파일들 먼저 복사 (캐시 최적화)
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/
COPY gradlew ./

# 의존성만 먼저 다운로드 (캐시 레이어)
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 빌드 실행 (테스트 제외)
RUN gradle clean build -x test --no-daemon

# 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]