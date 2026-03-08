FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY device-emulator/ device-emulator/
RUN echo "rootProject.name = 'notification-system'" > settings.gradle && \
    echo "include 'device-emulator'" >> settings.gradle && \
    chmod +x gradlew && ./gradlew :device-emulator:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/device-emulator/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
