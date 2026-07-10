# Docker Setup for Spring AI Demo React

## Quick Start

### Option 1: Full Stack with Docker Compose (Recommended)

```bash
# From spring-ai-demo-react directory
export MY_APP_KEY=sk-your-openai-api-key

# Build and run both frontend and backend
docker-compose up -d

# View logs
docker-compose logs -f

# Stop everything
docker-compose down
```

### Option 2: React Frontend Only

```bash
# Build
docker build -t spring-ai-demo-react:latest .

# Run
docker run -d \
  --name spring-ai-demo-react \
  -p 3000:80 \
  spring-ai-demo-react:latest
```

## Architecture

### Frontend (React)
- **Image**: `spring-ai-demo-react:latest`
- **Port**: 3000 (external), 80 (internal)
- **Base**: `nginx:alpine`
- **Size**: ~150MB
- **Build Time**: ~3-5 minutes (with npm install)

### Backend (Spring Boot)
- **Image**: `spring-ai-demo:latest`
- **Port**: 8080
- **Base**: `eclipse-temurin:17-jre-alpine`
- **Size**: 343MB

## Dockerfile Overview

### Multi-stage Build Process

**Stage 1: Builder**
- Uses `node:18-alpine` image
- Installs npm dependencies
- Builds optimized React production bundle
- Output: `/app/build` directory

**Stage 2: Production**
- Uses `nginx:alpine` image
- Copies built bundle to nginx HTML directory
- Includes custom nginx configuration
- Minimal image size (~150MB)

## Features

### React App Features
- ✅ SPA (Single Page Application) routing with index.html fallback
- ✅ Static file caching (1 year for assets)
- ✅ Gzip compression enabled
- ✅ API proxy to Spring Boot backend
- ✅ Health check endpoint

### Nginx Configuration
- **SPA Routing**: All requests route to index.html
- **API Proxy**: `/api/*` proxied to backend
- **Caching**: JS/CSS/images cached 1 year
- **Compression**: Gzip enabled for text/json
- **Security**: No directory indexing

## Usage

### Access the Application
```bash
# Frontend
http://localhost:3000

# Backend API
http://localhost:8080

# Full stack via docker-compose
http://localhost:3000  # Frontend
http://localhost:8080  # Backend
```

### Docker Compose Commands

```bash
# Start services
docker-compose up -d

# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f spring-ai-demo-react
docker-compose logs -f spring-ai-demo

# Stop services
docker-compose down

# Rebuild images
docker-compose build --no-cache

# View service status
docker-compose ps
```

### Docker CLI Commands

```bash
# Build frontend image
docker build -t spring-ai-demo-react:latest .

# Run frontend container
docker run -d \
  -p 3000:80 \
  --name react-app \
  spring-ai-demo-react:latest

# Build and tag for registry
docker tag spring-ai-demo-react:latest your-username/spring-ai-demo-react:latest

# Push to registry
docker push your-username/spring-ai-demo-react:latest
```

## Environment Variables

### Frontend (React)
- `REACT_APP_API_URL`: Backend API endpoint (set in docker-compose.yml)

### Backend (Spring Boot)
- `MY_APP_KEY`: OpenAI API key (required)
- `JAVA_OPTS`: JVM options (optional)

## Networking

### Single Network (Docker Compose)
When using docker-compose, both services are on the same network:
```bash
# From React container, access backend at:
http://spring-ai-demo:8080
```

### API Proxy Configuration
The nginx.conf includes proxy configuration:
```nginx
location /api/ {
    proxy_pass http://spring-ai-demo:8080/;
}
```

## Performance

### Build Time
- **First build**: ~5-10 minutes (npm install + build)
- **Subsequent builds**: ~1-2 minutes (npm cache)
- **Docker build cache**: Speeds up rebuilds

### Runtime Performance
- **Startup time**: ~2-3 seconds
- **First page load**: <1 second (after build)
- **Memory usage**: ~50-100MB
- **CPU**: Minimal when idle

### Optimization
- Node modules excluded from image (installed during build)
- Built assets minified and optimized
- Gzip compression enabled
- Browser caching enabled (1 year for versioned assets)

## Size Optimization

### Image Sizes
```
Builder stage:     ~350MB (node:18-alpine + dependencies)
Runtime stage:     ~150MB (nginx:alpine + built app)
Final image:       ~150MB
```

### Why Small?
- Alpine Linux base (~5MB vs 150MB+ with debian)
- Nginx lightweight (~10MB vs Apache with ~50MB+)
- Node modules not included (only final built bundle)
- Production build minified (~10-20MB)

## Building for Different Environments

### Development
```bash
# Build with dev dependencies
docker build -t spring-ai-demo-react:dev --target builder .
docker run -it spring-ai-demo-react:dev npm start
```

### Production
```bash
# Default build (production optimized)
docker build -t spring-ai-demo-react:latest .
```

### Staging
```bash
# Add build argument
docker build --build-arg ENV=staging -t spring-ai-demo-react:staging .
```

## Troubleshooting

### Container won't start
```bash
docker logs spring-ai-demo-react
```

### Port already in use
```bash
# Use different port
docker run -p 3001:80 spring-ai-demo-react:latest
```

### Cannot reach backend API
```bash
# Check if backend is running
docker ps

# Check docker network
docker network ls
docker network inspect docker_default  # for docker-compose

# Check nginx proxy configuration
docker exec spring-ai-demo-react cat /etc/nginx/nginx.conf
```

### Build fails with npm error
```bash
# Clear npm cache
docker build --no-cache -t spring-ai-demo-react:latest .

# Use npm ci instead of npm install
# Already configured in Dockerfile
```

### React app shows blank page
```bash
# Check browser console for errors
# Verify API_URL is correct
# Check nginx configuration for correct root path
docker exec spring-ai-demo-react cat /etc/nginx/nginx.conf
```

## Advanced Configuration

### Custom Environment Variables
```yaml
# docker-compose.yml
environment:
  - REACT_APP_API_URL=http://backend-api.example.com
  - REACT_APP_TIMEOUT=30000
```

### Volume Mounting (Development)
```bash
docker run -d \
  -p 3000:80 \
  -v $(pwd)/build:/usr/share/nginx/html \
  spring-ai-demo-react:latest
```

### Resource Limits
```yaml
deploy:
  resources:
    limits:
      cpus: '0.5'
      memory: 512M
    reservations:
      cpus: '0.25'
      memory: 256M
```

## Production Deployment

### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-ai-demo-react
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-ai-demo-react
  template:
    metadata:
      labels:
        app: spring-ai-demo-react
    spec:
      containers:
      - name: react-app
        image: spring-ai-demo-react:latest
        ports:
        - containerPort: 80
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /health
            port: 80
          initialDelaySeconds: 5
          periodSeconds: 30
```

### AWS ECS
```json
{
  "family": "spring-ai-demo-react",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "react-app",
      "image": "your-account.dkr.ecr.region.amazonaws.com/spring-ai-demo-react:latest",
      "portMappings": [{"containerPort": 80}],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/spring-ai-demo-react"
        }
      }
    }
  ]
}
```

## CI/CD Integration

### GitHub Actions
```yaml
- name: Build Docker image
  run: |
    docker build -t spring-ai-demo-react:${{ github.sha }} .
    docker tag spring-ai-demo-react:${{ github.sha }} spring-ai-demo-react:latest

- name: Push to Docker Hub
  run: |
    docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
    docker push spring-ai-demo-react:latest
```

### GitLab CI
```yaml
build:
  stage: build
  image: docker:latest
  script:
    - docker build -t spring-ai-demo-react:latest .
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
    - docker push spring-ai-demo-react:latest
```

## Health Checks

### Endpoint
- **Health Check**: GET `/health` returns `healthy`
- **Interval**: 30 seconds
- **Timeout**: 10 seconds
- **Start Period**: 5 seconds
- **Retries**: 3

### Check Status
```bash
docker ps  # Look for "healthy" or "unhealthy"
docker inspect spring-ai-demo-react --format='{{.State.Health.Status}}'
```

## Monitoring

### View Statistics
```bash
docker stats spring-ai-demo-react
```

### Check Logs
```bash
docker logs spring-ai-demo-react
docker logs -f spring-ai-demo-react  # Follow logs
```

### Inspect Container
```bash
docker inspect spring-ai-demo-react
```

## Maintenance

### Update React Dependencies
```bash
# Update package.json locally, then:
docker-compose build --no-cache
docker-compose up -d
```

### Cleanup
```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove all unused data
docker system prune -a
```

## Additional Resources

- Docker Documentation: https://docs.docker.com/
- Nginx Documentation: https://nginx.org/en/docs/
- React Deployment: https://create-react-app.dev/docs/deployment/
- Docker Best Practices: https://docs.docker.com/develop/dev-best-practices/
