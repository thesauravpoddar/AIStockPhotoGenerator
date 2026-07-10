# Docker Setup for Spring AI Demo

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Set your OpenAI API key as environment variable
export MY_APP_KEY=sk-your-openai-api-key
# If you do not use bash or zsh, you can set the environment variable in your shell's way

# Build and run the application
cd SpringAiDemo
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

### Option 2: Using Docker CLI

#### Build the image
```bash
cd SpringAiDemo
docker build -t spring-ai-demo:latest .
```

#### Run the container
```bash
docker run -d \
  --name spring-ai-demo \
  -p 8080:8080 \
  -e MY_APP_KEY=sk-your-openai-api-key \
  spring-ai-demo:latest
```

#### View logs
```bash
docker logs -f spring-ai-demo
```

#### Stop the container
```bash
docker stop spring-ai-demo
docker rm spring-ai-demo
```

## Dockerfile Overview

The Dockerfile uses a multi-stage build approach:

**Stage 1: Builder**
- Uses `maven:3.9-eclipse-temurin-17` image
- Installs dependencies
- Compiles and packages the application

**Stage 2: Runtime**
- Uses lightweight `eclipse-temurin:17-jre-alpine` image
- Copies only the built JAR file
- Minimal size (~300MB vs 500MB+ with full JDK)

## Configuration

### Environment Variables

| Variable | Description | Required | Example |
|----------|-------------|----------|---------|
| `MY_APP_KEY` | OpenAI API Key | Yes | `sk-...` |

### Ports

- **8080**: Spring Boot application (HTTP)

### Volume Mounts (Optional)

```bash
docker run -d \
  -p 8080:8080 \
  -e MY_APP_KEY=sk-your-api-key \
  -v /local/logs:/app/logs \
  spring-ai-demo:latest
```

## Networking

### Single Container
```bash
docker run -p 8080:8080 spring-ai-demo:latest
# Accessible at http://localhost:8080
```

### With Docker Compose Network
```bash
docker-compose up
# Service accessible as http://spring-ai-demo:8080 from other containers
```

### Connect to Custom Network
```bash
docker network create my-network
docker run -d --network my-network --name spring-ai-demo spring-ai-demo:latest
```

## API Endpoints

Once running, test the endpoints:

```bash
# Chat endpoint
curl "http://localhost:8080/ask-ai?prompt=Hello%20World"

# Recipe creator
curl "http://localhost:8080/recipe-creator?ingredients=tomato,basil&cuisine=italian"

# Image generation
curl "http://localhost:8080/generate-image?prompt=sunset&quality=hd&n=1"
```

## Troubleshooting

### Container won't start
```bash
docker logs spring-ai-demo
# Check if MY_APP_KEY is set correctly
```

### Port already in use
```bash
# Use a different port
docker run -p 9090:8080 spring-ai-demo:latest
```

### Out of memory
```bash
# Increase heap size
export JAVA_OPTS="-Xmx1024m -Xms512m"
docker-compose up
```

### API calls failing
```bash
# Verify the container has network access
docker exec spring-ai-demo ping api.openai.com
```

## Production Considerations

1. **Use Alpine Linux**: Keeps image size small (~300MB)
2. **Health Checks**: Configured for automatic restarts
3. **Non-root user**: Consider adding a non-root user
4. **Secrets Management**: Use Docker secrets or environment files
5. **Logging**: Mount volumes for persistent logs
6. **Resource Limits**: Set appropriate CPU/memory limits

## Build Options

### Build with specific Java version
```bash
docker build --build-arg JAVA_VERSION=21 -t spring-ai-demo:java21 .
```

### Build without cache
```bash
docker build --no-cache -t spring-ai-demo:latest .
```

## Size Optimization

Check image sizes:
```bash
docker images spring-ai-demo
```

Multi-stage build reduces image size:
- Full image: ~300MB (with Alpine JRE)
- Without multi-stage: ~700MB+

## Security

1. **Don't commit secrets**: Use environment variables
2. **Scan for vulnerabilities**: `docker scan spring-ai-demo`
3. **Use read-only filesystem**: `docker run --read-only spring-ai-demo`
4. **Run as non-root**: Add to Dockerfile (in production)

## Additional Resources

- Docker Documentation: https://docs.docker.com/
- Spring Boot Docker Guide: https://spring.io/guides/gs/spring-boot-docker/
- Maven Docker Plugin: https://github.com/spotify/dockerfile-maven
