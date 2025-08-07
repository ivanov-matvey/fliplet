FROM openjdk:17-jdk-slim
LABEL authors="matvenoid"

WORKDIR /app

COPY build/libs/backend-0.0.1.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
