# Use a lightweight JRE image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Expect the jar file to be in the same directory as the Dockerfile
COPY *.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
