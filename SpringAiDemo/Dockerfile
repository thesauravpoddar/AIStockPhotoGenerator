# Dockerfile for Spring AI Demo

# Use lightweight Java runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the pre-built JAR file
# Build with: mvn clean package
COPY target/SpringAiDemo-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Environment variable for OpenAI API key (must be provided at runtime)
ENV MY_APP_KEY=""

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
