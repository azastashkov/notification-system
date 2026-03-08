FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY third-party-emulator/ third-party-emulator/
RUN echo "rootProject.name = 'notification-system'" > settings.gradle && \
    echo "include 'third-party-emulator'" >> settings.gradle && \
    chmod +x gradlew && ./gradlew :third-party-emulator:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/third-party-emulator/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
