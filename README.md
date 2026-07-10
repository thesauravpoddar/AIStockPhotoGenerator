# This is a stock Photo generator using AI 
- follow the following steps to use the application
# Docker Setup for Spring AI Demo

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Set your OpenAI API key as environment variable
export MY_APP_KEY=sk-your-openai-api-key
# If you do not use bash or zsh, you can set the environment variable in your shell's way

# Build and run the application
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
# running the backend
docker run -d \
  --name spring-ai-demo \
  -p 8080:8080 \
  -e MY_APP_KEY=sk-your-openai-api-key \
  spring-ai-demo:latest
# running the fronted
docker build -t spring-ai-demo-react:latest .

# Run
docker run -d \
  --name spring-ai-demo-react \
  -p 3000:80 \
  spring-ai-demo-react:latest
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

## Environment Variables
### Backend (Spring Boot)
| Variable | Description | Required | Example |
|----------|-------------|----------|---------|
| `MY_APP_KEY` | OpenAI API Key | Yes | `sk-...` |

### Frontend (React)
- `REACT_APP_API_URL`: Backend API endpoint (set in docker-compose.yml)

## Access the Application
```bash
# Frontend
http://localhost:3000

# Backend API
http://localhost:8080

# Full stack via docker-compose
http://localhost:3000  # Frontend
http://localhost:8080  # Backend
```
