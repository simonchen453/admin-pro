# ==================================================
# Stage 1: Build Backend
# ==================================================
FROM maven:3.9.6-eclipse-temurin-21 AS backend-builder

WORKDIR /app

# Copy Maven settings.xml (配置镜像，将所有 HTTP 请求重定向到 HTTPS)
COPY docker/backend/settings.xml /root/.m2/settings.xml

# Copy Maven POMs first (for dependency caching)
COPY pom.xml .
COPY adminpro-system/pom.xml adminpro-system/
COPY adminpro-framework/pom.xml adminpro-framework/
COPY adminpro-web/pom.xml adminpro-web/

# Download dependencies (this layer will be cached if pom.xml doesn't change)
# 使用自定义 settings.xml 配置镜像，避免 HTTP 仓库访问问题
RUN mvn dependency:go-offline -B

# Copy backend source code
COPY adminpro-system/src adminpro-system/src
COPY adminpro-framework/src adminpro-framework/src
COPY adminpro-web/src adminpro-web/src

# Build the JAR
RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true

# ==================================================
# Stage 2: Runtime
# ==================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install required packages
RUN apk add --no-cache \
    font-adobe-100dpi \
    ttf-dejavu \
    fontconfig \
    tzdata \
    curl

# Set timezone
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create directories for logs and uploads
RUN mkdir -p /app/logs /app/upload/public /app/upload/private

# Copy the built JAR
COPY --from=backend-builder /app/adminpro-web/target/adminpro-web.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/adminpro/actuator/health || exit 1

# Start the application
ENTRYPOINT ["java", \
  "-Xms512m", \
  "-Xmx1024m", \
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=200", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar", \
  "--spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]

