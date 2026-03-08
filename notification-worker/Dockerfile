FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY notification-worker/ notification-worker/
RUN echo "rootProject.name = 'notification-system'" > settings.gradle && \
    echo "include 'notification-worker'" >> settings.gradle && \
    chmod +x gradlew && ./gradlew :notification-worker:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/notification-worker/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
