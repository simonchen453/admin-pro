# Docker Desktop 启动配置说明

## 方式一：使用 docker-compose（推荐）

### 1. 修改配置
编辑 `docker-compose.yml` 文件，修改以下关键配置：

```yaml
environment:
  # 数据库配置 - 必须修改
  - DB_HOST=host.docker.internal  # Mac 上访问宿主机 MySQL
  - DB_PORT=3306
  - DB_NAME=adminpro              # 您的数据库名
  - DB_USERNAME=adminpro          # 数据库用户名
  - DB_PASSWORD=your_password    # 数据库密码（必须修改）
```

### 2. 启动容器
```bash
# 构建镜像（首次或代码更新后）
docker build -t admin-pro:latest .

# 启动容器
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止容器
docker-compose down
```

---

## 方式二：使用 docker run 命令

### 基本启动命令
```bash
docker run -d \
  --name admin-pro \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=3306 \
  -e DB_NAME=adminpro \
  -e DB_USERNAME=adminpro \
  -e DB_PASSWORD=your_password \
  -e DB_USE_SSL=false \
  -v $(pwd)/logs:/app/logs \
  -v $(pwd)/upload:/app/upload \
  --restart unless-stopped \
  admin-pro:latest
```

---

## Docker Desktop 配置要点

### 1. 端口映射
- **容器端口**: 8080
- **宿主机端口**: 8080
- 访问地址: `http://localhost:8080/adminpro`

### 2. 数据库连接配置

#### 选项 A：使用 Mac 本地的 MySQL
```yaml
DB_HOST=host.docker.internal  # Docker Desktop 提供的特殊地址，用于访问宿主机
DB_PORT=3306
```

#### 选项 B：使用 Docker 中的 MySQL
```yaml
# 在 docker-compose.yml 中添加 MySQL 服务
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: adminpro
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

# 然后修改 admin-pro 的 DB_HOST
DB_HOST=mysql  # 使用服务名
```

#### 选项 C：使用远程数据库
```yaml
DB_HOST=your-db-server.com
DB_PORT=3306
```

### 3. 数据持久化（Volume 挂载）

必须挂载的目录：
- `./logs` → `/app/logs` - 日志文件
- `./upload` → `/app/upload` - 上传的文件

### 4. 资源限制（可选）

在 Docker Desktop 中设置：
1. 打开 Docker Desktop
2. 进入 Settings → Resources
3. 建议配置：
   - **Memory**: 至少 2GB（推荐 4GB）
   - **CPUs**: 至少 2 核
   - **Disk**: 至少 20GB

或在 `docker-compose.yml` 中配置（已包含）：
```yaml
deploy:
  resources:
    limits:
      cpus: '2.0'
      memory: 2G
```

### 5. 网络配置

- **默认**: 使用 Docker 的 bridge 网络
- **如需访问宿主机服务**: 使用 `host.docker.internal`（Mac/Windows Docker Desktop 自动支持）

---

## 常用命令

```bash
# 查看容器状态
docker ps

# 查看日志
docker logs -f admin-pro

# 进入容器
docker exec -it admin-pro sh

# 重启容器
docker restart admin-pro

# 停止容器
docker stop admin-pro

# 删除容器
docker rm admin-pro
```

---

## 故障排查

### 1. 数据库连接失败
- 检查 `DB_HOST` 是否正确
- Mac 上使用 `host.docker.internal` 访问宿主机 MySQL
- 确认 MySQL 允许远程连接（或 Docker 网络访问）

### 2. 端口被占用
```bash
# 检查端口占用
lsof -i :8080

# 修改 docker-compose.yml 中的端口映射
ports:
  - "8081:8080"  # 使用 8081 端口
```

### 3. 内存不足
- 在 Docker Desktop Settings 中增加内存分配
- 或减少 `docker-compose.yml` 中的内存限制

### 4. 查看详细日志
```bash
# 查看应用日志
docker logs admin-pro

# 查看 Spring Boot 启动日志
docker logs admin-pro | grep -i "started"
```

---

## 完整示例（docker-compose.yml）

已创建 `docker-compose.yml` 文件，包含所有必要配置。只需：
1. 修改数据库密码
2. 运行 `docker-compose up -d`

