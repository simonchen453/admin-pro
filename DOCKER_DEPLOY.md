# Docker 部署最佳实践指南

本项目采用前后端分离的 Docker 部署方案，前端使用 Nginx 服务静态文件，后端使用 Spring Boot 独立容器。**数据库使用外部独立服务，不包含在 Docker Compose 中。**

## 📋 目录结构

```
.
├── docker/
│   ├── frontend/
│   │   ├── Dockerfile          # 前端构建和运行镜像
│   │   └── nginx.conf          # Nginx 配置文件
│   └── backend/
│       └── Dockerfile          # 后端构建和运行镜像
├── docker-compose.yml          # 基础编排配置
├── docker-compose.prod.yml     # 生产环境覆盖配置
└── DOCKER_DEPLOY.md            # 本文档
```

## 🚀 快速开始

### 开发环境（本地开发）

开发环境前端和后端在本地运行，数据库使用外部服务：

```bash
# 1. 确保外部数据库服务已启动并配置好

# 2. 在本地运行前端
cd frontend
pnpm install
pnpm dev

# 3. 在本地运行后端（使用 IDE 或 Maven）
# 后端配置数据库连接: 根据实际情况配置数据库地址
```

### 生产环境（Docker 部署）

### 1. 准备环境变量

创建 `.env` 文件（可选），配置数据库连接等信息：

```bash
# 数据库配置（外部数据库）
DB_HOST=your-database-host
DB_PORT=3306
DB_NAME=adminpro
DB_USERNAME=adminpro
DB_PASSWORD=your_password
```

### 2. 启动服务

```bash
# 构建并启动（生产环境）
make up
# 或
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 3. 查看服务状态

```bash
# 查看所有容器状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
```

### 4. 访问应用

- **前端**: http://localhost/adminpro
- **后端 API**: http://localhost/api (前端代理) 或 http://localhost/adminpro/api (直接访问)
- **Swagger 文档**: http://localhost/adminpro/swagger-ui.html (开发环境)

**注意**: 前端使用 `/adminpro` 作为 contextPath，根路径 `/` 可留给业务代码使用。

## 💻 开发环境说明

### 本地开发流程

1. **确保数据库服务已启动**
   - 数据库使用外部独立服务
   - 根据实际情况配置数据库连接信息

2. **运行前端（本地）**
   ```bash
   cd frontend
   pnpm install
   pnpm dev
   # 前端将在 http://localhost:5173 运行（Vite 默认端口）
   ```

3. **运行后端（本地）**
   - 使用 IDE（如 IntelliJ IDEA）直接运行 Spring Boot 应用
   - 或使用 Maven：
     ```bash
     cd adminpro-web
     mvn spring-boot:run
     ```
   - 后端将在 http://localhost:8080 运行

4. **配置说明**
   - 前端开发服务器会自动代理 `/api` 到后端（见 `vite.config.ts`）
   - 后端需要配置数据库连接（根据实际情况配置）
   - 开发环境建议启用 Swagger: `APP_SWAGGER_ENABLED=true`

## 📦 服务说明

### 前端服务 (frontend)

- **镜像**: 基于 `nginx:alpine`
- **端口**: 80 (可通过 `FRONTEND_PORT` 环境变量修改)
- **ContextPath**: `/adminpro` (根路径 `/` 可留给业务代码使用)
- **功能**:
  - 服务前端静态文件（路径：`/adminpro/`）
  - 代理 `/api` 请求到后端
  - 支持 SPA 路由
  - Gzip 压缩
  - 静态资源缓存

### 后端服务 (backend)

- **镜像**: 基于 `eclipse-temurin:21-jre-alpine`
- **端口**: 8080 (可通过 `BACKEND_PORT` 环境变量修改)
- **功能**:
  - Spring Boot 应用
  - 健康检查端点: `/adminpro/actuator/health`
  - 日志持久化到 `./logs`
  - 上传文件持久化到 `./upload`

### 数据库服务

- **说明**: 数据库使用外部独立服务，不包含在 Docker Compose 中
- **配置**: 通过环境变量 `DB_HOST`、`DB_PORT` 等配置数据库连接
- **Mac/Windows**: Docker 容器通过 `host.docker.internal` 访问宿主机数据库（已自动配置）
- **Linux**: 
  - 如果数据库在宿主机上，使用 `host.docker.internal`（已通过 `extra_hosts` 配置）
  - 如果数据库在其他服务器，直接使用数据库的实际 IP 地址或域名

## 🔧 配置说明

### Docker Desktop 配置要点

如果使用 Docker Desktop（Mac/Windows），建议配置以下资源：

1. 打开 Docker Desktop
2. 进入 Settings → Resources
3. 建议配置：
   - **Memory**: 至少 2GB（推荐 4GB）
   - **CPUs**: 至少 2 核
   - **Disk**: 至少 20GB

### 环境变量配置

主要配置项在 `.env` 文件中（可选，也可直接使用环境变量）：

```bash
# 数据库配置（外部数据库）
DB_HOST=host.docker.internal  # Mac/Windows 使用宿主机数据库
# DB_HOST=192.168.1.100       # Linux 或使用远程数据库
# DB_HOST=mysql               # 如果数据库也在 Docker 网络中

DB_PORT=3306
DB_NAME=adminpro
DB_USERNAME=adminpro
DB_PASSWORD=your_password     # ⚠️ 必须修改

# 其他配置
BACKEND_PORT=8080
FRONTEND_PORT=80
```

### 端口映射

默认端口映射：
- 前端: `80:80`
- 后端: `8080:8080`

可通过环境变量修改：
```bash
FRONTEND_PORT=80
BACKEND_PORT=8080
```

### 修改部署路径 (Context Path)

默认部署路径为 `/adminpro`。如需修改（例如改为 `/dashboard`），**无需重新构建镜像**，只需修改环境变量配置：

1. **修改环境变量**  
   在 `docker-compose.yml` 或 `.env` 文件中设置 `APP_BASE_PATH`：
   ```bash
   APP_BASE_PATH=/dashboard
   ```

2. **重启服务**  
   修改配置后，重启容器即可生效（容器启动时会自动替换静态资源中的路径）：
   ```bash
   docker-compose up -d
   ```

**注意**：
- 新的路径格式建议为 `/path`（不要以 `/` 结尾）。
- 容器启动脚本会自动处理前端静态资源的路径引用。

## 🛠️ 常用命令

### 构建镜像

```bash
# 构建所有服务
docker-compose build

# 构建特定服务
docker-compose build frontend
docker-compose build backend

# 强制重新构建（不使用缓存）
docker-compose build --no-cache
```

### 启动和停止

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 停止并删除 volumes（⚠️ 会删除数据库数据）
docker-compose down -v

# 重启服务
docker-compose restart backend
```

### 查看日志

```bash
# 查看所有日志
docker-compose logs -f

# 查看最近 100 行日志
docker-compose logs --tail=100

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
```

### 进入容器

```bash
# 进入后端容器
docker-compose exec backend sh

# 进入前端容器
docker-compose exec frontend sh


### 健康检查

```bash
# 检查服务健康状态
docker-compose ps

# 手动检查后端健康
curl http://localhost:8080/adminpro/actuator/health

# 手动检查前端健康
curl http://localhost/health
```

## 🔄 更新部署

### 更新代码后重新部署

```bash
# 1. 拉取最新代码
git pull

# 2. 重新构建镜像
docker-compose build

# 3. 重启服务（零停机时间，使用新镜像）
docker-compose up -d

# 或强制重新创建容器
docker-compose up -d --force-recreate
```

### 仅更新前端

```bash
docker-compose build frontend
docker-compose up -d frontend
```

### 仅更新后端

```bash
docker-compose build backend
docker-compose up -d backend
```

## 📊 生产环境部署（Docker）

### 使用生产配置

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

生产环境配置特点：
- 关闭 SQL 日志
- 关闭 Swagger
- 启用管理端点安全
- 增加资源限制
- 配置日志轮转

### 资源限制

生产环境建议配置：
- **后端**: 4GB 内存，4 CPU
- **前端**: 512MB 内存，1 CPU
- **MySQL**: 2GB 内存，2 CPU

### 日志管理

生产环境日志配置：
- 单文件最大: 10MB
- 保留文件数: 3
- 日志目录: `./logs`

## 🔒 安全建议

1. **修改默认密码**
   ```bash
   # 在 .env 中修改
   DB_PASSWORD=your_strong_password
   MYSQL_ROOT_PASSWORD=your_strong_root_password
   ```

2. **使用 HTTPS**
   - 在 Nginx 配置中添加 SSL 证书
   - 或使用外部反向代理（如 Traefik、Nginx Proxy）

3. **限制网络访问**
   ```yaml
   # 在 docker-compose.yml 中
   backend:
     ports:
       - "127.0.0.1:8080:8080"  # 仅本地访问
   ```

4. **定期更新镜像**
   ```bash
   docker-compose pull
   docker-compose up -d
   ```

## 🐛 故障排查

### 后端无法启动

```bash
# 查看后端日志
docker-compose logs backend

# 检查数据库连接
docker-compose exec backend sh
# 在容器内测试数据库连接
```

### 前端无法访问后端

```bash
# 检查 Nginx 配置
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf

# 检查后端健康状态
curl http://localhost:8080/adminpro/actuator/health

# 检查网络连接
docker-compose exec frontend ping backend
```

### 数据库连接失败

```bash
# 检查数据库连接配置
echo $DB_HOST
echo $DB_PORT

# 从容器内测试数据库连接
docker-compose exec backend sh
# 在容器内使用 ping 或 telnet 测试数据库连接

# 检查 extra_hosts 配置（Mac/Windows）
# 确保 docker-compose.yml 中包含 extra_hosts 配置

# Mac/Windows 上使用 host.docker.internal 访问宿主机 MySQL
# 确保 MySQL 允许 Docker 网络访问
# 检查 MySQL 是否运行
# macOS: brew services list | grep mysql
# 或直接测试: mysql -h host.docker.internal -u adminpro -p
```

### 端口冲突

```bash
# 检查端口占用
lsof -i :80
lsof -i :8080

# 修改 .env 中的端口配置
FRONTEND_PORT=8081
BACKEND_PORT=8082

# 或在 docker-compose.yml 中直接修改端口映射
ports:
  - "8081:80"    # 前端
  - "8082:8080"  # 后端
```

### 内存不足

如果遇到内存不足的问题：

```bash
# 在 Docker Desktop Settings 中增加内存分配
# 或减少 docker-compose.yml 中的内存限制

# 查看容器资源使用情况
docker stats
```

## 📝 最佳实践

1. **使用 .env 文件管理配置**
   - 不要将 `.env` 提交到 Git
   - 使用 `.env.example` 作为模板

2. **数据持久化**
   - 日志目录: `./logs`
   - 上传文件: `./upload`
   - 数据库: 使用外部数据库服务

3. **健康检查**
   - 所有服务都配置了健康检查
   - 后端启动前请确保数据库服务可用

4. **资源限制**
   - 生产环境设置合理的资源限制
   - 避免容器占用过多资源

5. **日志管理**
   - 配置日志轮转
   - 定期清理旧日志

6. **备份策略**
   - 定期备份外部数据库
   - 备份上传文件目录

## 🔗 相关文档

- [Docker Compose 官方文档](https://docs.docker.com/compose/)
- [Nginx 配置文档](https://nginx.org/en/docs/)
- [Spring Boot Docker 最佳实践](https://spring.io/guides/gs/spring-boot-docker/)

