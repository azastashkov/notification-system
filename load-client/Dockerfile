FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY load-client/ load-client/
RUN chmod +x gradlew && ./gradlew :load-client:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/load-client/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
