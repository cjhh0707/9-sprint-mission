FROM amazoncorretto:17 AS builder
WORKDIR /app

# 의존성 레이어 캐시: gradle 설정 파일을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 코드 복사 후 빌드
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# 런타임 스테이지: JRE 전용 경량 이미지 사용
FROM amazoncorretto:17-al2023-headless
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar .

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
