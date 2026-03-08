FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY notification-server/ notification-server/
RUN echo "rootProject.name = 'notification-system'" > settings.gradle && \
    echo "include 'notification-server'" >> settings.gradle && \
    chmod +x gradlew && ./gradlew :notification-server:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/notification-server/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
