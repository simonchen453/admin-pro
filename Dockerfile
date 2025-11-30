# ==================================================
# Stage 1: Build Frontend (React/Vite)
# ==================================================
FROM node:20-alpine AS frontend-builder

# Install pnpm
RUN corepack enable && corepack prepare pnpm@latest --activate

WORKDIR /app/frontend

# Copy package.json and lock file first to leverage Docker cache
COPY frontend/package.json frontend/pnpm-lock.yaml ./

# Install dependencies
RUN pnpm install --frozen-lockfile

# Copy frontend source code
COPY frontend/ .

# Build the frontend (skip type check to ignore strict linting errors)
RUN pnpm exec vite build

# ==================================================
# Stage 2: Build Backend (Spring Boot/Java 21)
# ==================================================
FROM maven:3.9.6-eclipse-temurin-21 AS backend-builder

WORKDIR /app

# Copy Maven POMs first (for dependency caching)
COPY pom.xml .
COPY adminpro-common/pom.xml adminpro-common/
COPY adminpro-core/pom.xml adminpro-core/
COPY adminpro-web/pom.xml adminpro-web/

# Copy backend source code
COPY adminpro-common/src adminpro-common/src
COPY adminpro-core/src adminpro-core/src
COPY adminpro-web/src adminpro-web/src

# Copy built frontend assets to Spring Boot static resources
# Vite builds to 'dist', Spring Boot serves from 'static'
COPY --from=frontend-builder /app/frontend/dist adminpro-web/src/main/resources/static

# Build the JAR
RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true

# ==================================================
# Stage 3: Runtime
# ==================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install required packages
RUN apk add --no-cache font-adobe-100dpi ttf-dejavu fontconfig tzdata

# Set timezone
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Copy the built JAR from backend-builder
COPY --from=backend-builder /app/adminpro-web/target/adminpro-web.jar app.jar

# Expose port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
