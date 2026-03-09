# 1. Base image (lightweight JVM)
FROM eclipse-temurin:17-jre-alpine

# 2. App directory inside container
WORKDIR /app

# 3. Copy jar
COPY target/order-service-0.0.1-SNAPSHOT.jar app.jar

# 4. Expose port (logical, not mandatory)
EXPOSE 8080

# 5. Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
