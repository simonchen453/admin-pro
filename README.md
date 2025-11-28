# Admin Pro 管理系统

<div align="center">
  <h3>企业级权限管理系统</h3>
  <p>基于 Spring Boot + React 的现代化管理平台</p>
  <p>
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=spring" alt="Spring Boot Version" />
    <img src="https://img.shields.io/badge/React-19.1.1-blue?logo=react" alt="React Version" />
    <img src="https://img.shields.io/badge/TypeScript-5.9.3-blue?logo=typescript" alt="TypeScript Version" />
    <img src="https://img.shields.io/badge/Java-21-orange?logo=java" alt="Java Version" />
    <img src="https://img.shields.io/badge/MySQL-8.0+-blue?logo=mysql" alt="MySQL Version" />
  </p>
</div>

## 项目简介

Admin Pro 是一个前后端分离的企业级权限管理系统，提供完整的用户管理、角色权限、菜单管理、组织架构等核心功能。系统采用现代化的技术栈，具有良好的扩展性和可维护性。

## 技术栈

### 后端技术

- **框架**: Spring Boot 3.5.6
- **语言**: Java 21
- **构建工具**: Maven 3.9+
- **数据库**: MySQL 8.0+
- **数据库版本管理**: Liquibase
- **缓存**: Spring Cache (Simple/Redis/JCache)
- **API文档**: springdoc-openapi 2.6.0 (OpenAPI 3.0)
- **模板引擎**: FreeMarker (代码生成)
- **对象映射**: ModelMapper 3.2.0
- **JSON处理**: Gson 2.11.0
- **其他**: 
  - EasyPOI (Excel导入导出)
  - Kaptcha (验证码)
  - Oshi (系统信息监控)

### 前端技术

- **框架**: React 19.1.1
- **构建工具**: Vite 7.1.7
- **语言**: TypeScript 5.9.3
- **UI组件库**: Ant Design 5.27.4
- **状态管理**: Zustand 5.0.8
- **路由**: React Router DOM 7.9.4
- **HTTP客户端**: Axios 1.12.2
- **表单处理**: React Hook Form + Zod
- **包管理器**: pnpm 9.0.0

## 项目结构

```
admin-pro/
├── adminpro-common/          # 公共模块
│   ├── src/main/java/com/adminpro/
│   │   ├── api/               # API控制器（APK相关）
│   │   ├── config/            # 配置类
│   │   │   ├── ApiWebSecurityConfig.java    # API安全配置
│   │   │   ├── CaptchaConfig.java           # 验证码配置
│   │   │   ├── SecurityConfig.java          # 安全配置
│   │   │   ├── SessionConfig.java           # Session配置
│   │   │   ├── SwaggerConfig.java           # Swagger配置
│   │   │   └── ...
│   │   ├── framework/         # 框架核心
│   │   │   ├── batchjob/      # 批处理任务
│   │   │   │   ├── config/    # 任务配置
│   │   │   │   └── task/       # 任务实现
│   │   │   ├── cache/         # 缓存
│   │   │   ├── common/        # 通用工具
│   │   │   │   ├── annotation/  # 注解（如 @SysLog）
│   │   │   │   ├── aspect/      # AOP切面
│   │   │   │   ├── helper/      # 辅助类
│   │   │   │   └── web/         # Web基础类
│   │   │   ├── exceptions/   # 异常处理
│   │   │   ├── filters/      # 过滤器
│   │   │   ├── security/     # 安全相关
│   │   │   │   ├── auth/      # 认证
│   │   │   │   └── service/   # 权限服务
│   │   │   └── manager/       # 管理器
│   │   ├── rbac/             # RBAC权限模块
│   │   │   ├── api/          # 权限API辅助类
│   │   │   ├── domains/       # 领域模型
│   │   │   │   ├── entity/   # 实体类（User、Role、Menu等）
│   │   │   │   └── vo/        # 视图对象
│   │   │   ├── encrypt/      # 加密工具
│   │   │   └── enums/        # 枚举类
│   │   ├── tools/            # 工具模块
│   │   │   ├── api/          # 工具API（OSS、支付等）
│   │   │   ├── domains/      # 领域模型
│   │   │   ├── gen/          # 代码生成
│   │   │   ├── lock/         # 分布式锁
│   │   │   ├── payment/      # 支付（支付宝、微信）
│   │   │   ├── ueditor/      # 富文本编辑器
│   │   │   └── wx/           # 微信相关
│   │   └── web/              # Web控制器
│   │       ├── rbac/         # 权限相关控制器
│   │       │   ├── AuthController.java      # 认证控制器
│   │       │   ├── UserController.java      # 用户管理
│   │       │   ├── RoleController.java      # 角色管理
│   │       │   ├── MenuController.java       # 菜单管理
│   │       │   ├── DeptController.java       # 部门管理
│   │       │   └── ...
│   │       ├── tools/        # 工具相关控制器
│   │       │   ├── CodeGeneratorController.java  # 代码生成
│   │       │   ├── JobController.java        # 定时任务
│   │       │   ├── ConfigController.java     # 参数配置
│   │       │   ├── DictController.java       # 字典管理
│   │       │   └── ...
│   │       └── vo/           # 视图对象
│   └── src/main/resources/
│       ├── changelog/        # Liquibase变更日志
│       └── templates/        # FreeMarker模板
├── adminpro-core/            # 核心业务模块
│   └── src/main/java/com/adminpro/core/
│       └── base/             # 基础类
├── adminpro-web/             # Web启动模块
│   ├── src/main/java/com/adminpro/
│   │   └── Application.java # 启动类
│   └── src/main/resources/
│       ├── application.yml   # 应用配置
│       ├── application-dev.yml  # 开发环境配置
│       ├── application-prod.yml # 生产环境配置
│       ├── changelog/        # Liquibase变更日志
│       ├── logback/         # 日志配置
│       └── config/          # 其他配置
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   │   ├── request.ts    # Axios配置
│   │   │   ├── auth.ts       # 认证API
│   │   │   ├── user.ts       # 用户API
│   │   │   ├── role.ts       # 角色API
│   │   │   └── ...
│   │   ├── components/       # 公共组件
│   │   │   ├── Captcha.tsx   # 验证码组件
│   │   │   ├── ErrorBoundary.tsx  # 错误边界
│   │   │   ├── ProtectedRoute.tsx # 受保护路由
│   │   │   └── PublicRoute.tsx    # 公开路由
│   │   ├── pages/           # 页面组件
│   │   │   ├── Layout.tsx   # 主布局
│   │   │   ├── Login/       # 登录页面
│   │   │   ├── User/        # 用户管理
│   │   │   ├── Role/        # 角色管理
│   │   │   ├── Menu/        # 菜单管理
│   │   │   └── ...
│   │   ├── router/          # 路由配置
│   │   │   └── AppRouter.tsx
│   │   ├── stores/          # 状态管理
│   │   │   └── useUserStore.ts
│   │   ├── types/           # TypeScript类型定义
│   │   ├── utils/           # 工具函数
│   │   └── config/          # 配置文件
│   ├── public/              # 静态资源
│   ├── package.json
│   ├── vite.config.ts       # Vite配置
│   └── tsconfig.json        # TypeScript配置
├── docker/                  # Docker配置
│   ├── context.xml
│   ├── server.xml
│   └── run.sh
├── pom.xml                  # Maven父POM
└── Dockerfile               # Docker构建文件
```

## 环境要求

### 后端环境

- **JDK 21+** (必需，Spring Boot 3.x 要求 Java 17+)
- Maven 3.9+
- MySQL 8.0+ (推荐) 或 5.7+

### 前端环境

- Node.js 18+
- pnpm 9.0.0+ (推荐) 或 npm/yarn

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd admin-pro
```

### 2. 数据库配置

创建 MySQL 数据库：

```sql
CREATE DATABASE adminpro DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `adminpro-web/src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    master:
      jdbc-url: jdbc:mysql://127.0.0.1:3306/adminpro?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull
      username: your_username
      password: your_password
```

系统使用 Liquibase 自动管理数据库版本，首次启动会自动创建表结构。

### 3. 启动后端

```bash
# 进入项目根目录
cd admin-pro

# 确保使用 Java 21
java -version  # 应显示 java version "21.x.x"

# 编译项目
mvn clean install

# 启动应用
cd adminpro-web
mvn spring-boot:run

# 或者直接运行 JAR 文件
java -jar target/adminpro-web.jar
```

后端服务将在 `http://localhost:8080/adminpro` 启动。

**注意**: 
- 确保系统已安装 JDK 21
- 如果使用 IDE，请配置项目使用 Java 21
- Windows 系统可能需要设置 `JAVA_HOME` 环境变量指向 JDK 21

### 4. 启动前端

```bash
# 进入前端目录
cd frontend

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

前端开发服务器将在 `http://localhost:3000` 启动。

### 5. 访问系统

- **前端地址**: http://localhost:3000
- **后端API**: http://localhost:8080/adminpro
- **API文档**: http://localhost:8080/adminpro/swagger-ui.html
- **API JSON**: http://localhost:8080/adminpro/v3/api-docs

### 6. 默认账号

首次启动后，系统会自动创建默认管理员账号（如果数据库为空）：
- **用户名**: admin
- **密码**: admin123（请首次登录后修改）

**注意**: 生产环境部署前，请务必修改默认密码！

## 配置说明

### 后端配置

主要配置文件：`adminpro-web/src/main/resources/application.yml`

#### 数据库配置

```yaml
spring:
  datasource:
    master:
      jdbc-url: jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3306}/${DB_NAME:adminpro}?...
      username: ${DB_USERNAME:adminpro}
      password: ${DB_PASSWORD:password$1}
```

支持环境变量配置：
- `DB_HOST`: 数据库主机
- `DB_PORT`: 数据库端口
- `DB_NAME`: 数据库名称
- `DB_USERNAME`: 数据库用户名
- `DB_PASSWORD`: 数据库密码

#### 邮件配置

```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.example.com}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
```

#### 缓存配置

系统支持三种缓存类型：

```yaml
spring:
  cache:
    # 支持三种缓存类型：simple、redis、jcache
    type: jcache  # 默认使用 jcache (EhCache 3.x)
    # type: simple   # 简单内存缓存（Spring Boot 自带，无需额外配置）
    # type: redis    # Redis 缓存（需要 RedisCacheConfig 配置类）
    # type: jcache   # EhCache 3.x 缓存（需要 EhcacheConfig 配置类）
```

**缓存类型说明**：
- **simple**: Spring Boot 自带的简单内存缓存（`SimpleCacheManager`），适用于单机环境，无需额外配置或自定义实现类
- **redis**: Redis 缓存，由项目中的 `RedisCacheConfig` 配置类提供，需要配置 Redis 连接
- **jcache**: EhCache 3.x 缓存，由项目中的 `EhcacheConfig` 配置类提供，通过 JCache 标准实现，需要 `ehcache.xml` 配置文件

**使用 Redis 缓存时，需要配置 Redis 连接**：
```yaml
spring:
  cache:
    type: redis
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 3
      password: your_password  # 如果有密码
```

**使用 jcache (EhCache) 时，需要配置文件**：
- 配置文件位置：`adminpro-core/src/main/resources/ehcache.xml`
- 由 `EhcacheConfig` 配置类自动加载该配置文件

**注意**：
- `simple` 缓存是 Spring Boot 自带的，项目中没有自定义实现类
- `redis` 和 `jcache` 缓存由项目中的配置类提供（`RedisCacheConfig` 和 `EhcacheConfig`）

#### 文件上传配置

```yaml
app:
  upload:
    public:
      dir: ${UPLOAD_PUBLIC_DIR:file:./upload/public/}
    private:
      dir: ${UPLOAD_PRIVATE_DIR:file:./upload/private/}
```

#### CORS 配置

```yaml
app:
  cors:
    allowed-origins: ${APP_CORS_ORIGINS:http://localhost:3000,http://localhost:5173}
```

### 前端配置

主要配置文件：`frontend/vite.config.ts`

开发环境代理配置：

```typescript
proxy: {
  '/api': {
    target: 'http://127.0.0.1:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '/adminpro'),
  },
}
```

## 功能模块

### 核心功能

- ✅ **用户认证**
  - 登录/登出
  - 验证码验证
  - Session 管理
  - 密码修改

- ✅ **用户管理**
  - 用户列表（分页、搜索、筛选）
  - 用户新增/编辑/删除
  - 用户详情查看
  - 用户状态管理
  - 密码重置

- ✅ **权限管理 (RBAC)**
  - 角色管理（CRUD）
  - 角色菜单权限配置
  - 菜单管理（树形结构）
  - 权限路由保护

- ✅ **组织架构**
  - 部门管理（树形结构）
  - 岗位管理
  - 用户域管理
  - 用户域环境配置

- ✅ **系统配置**
  - 参数配置管理
  - 字典管理
  - 系统信息展示

- ✅ **任务调度**
  - 定时任务管理
  - 任务执行日志
  - Cron 表达式配置

- ✅ **监控运维**
  - 服务器信息监控
  - 系统日志查看
  - 审计日志记录
  - 在线会话管理

- ✅ **开发工具**
  - 代码生成器（基于 FreeMarker 模板）
    - 支持生成 React + TypeScript 前端代码
    - 支持生成 Java 后端代码（Entity、Service、Controller、DAO 等）
  - OpenAPI 3.0 文档（springdoc-openapi）

## 构建部署

### 后端构建

```bash
# 确保使用 Java 21
java -version

# 编译打包（跳过测试）
mvn clean package -DskipTests

# 生成的jar文件位置
# adminpro-web/target/adminpro-web.jar
```

**构建要求**:
- JDK 21 必须已安装并配置
- Maven 3.9+ 推荐
- 确保 `JAVA_HOME` 环境变量指向 JDK 21

### 前端构建

```bash
cd frontend

# 构建生产版本
pnpm build

# 构建产物在 dist/ 目录
```

### Docker 部署

#### 构建镜像

```bash
# 构建镜像
docker build -t admin-pro:latest .
```

#### 运行容器

```bash
# 基础运行
docker run -d -p 8080:8080 \
  -e DB_HOST=your_db_host \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  admin-pro:latest

# 完整配置运行
docker run -d -p 8080:8080 \
  --name admin-pro \
  -e DB_HOST=127.0.0.1 \
  -e DB_PORT=3306 \
  -e DB_NAME=adminpro \
  -e DB_USERNAME=adminpro \
  -e DB_PASSWORD=your_password \
  -e APP_CORS_ORIGINS=http://localhost:3000 \
  -v /path/to/upload:/app/upload \
  -v /path/to/logs:/app/logs \
  admin-pro:latest
```

#### Docker Compose 部署

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  admin-pro:
    build: .
    container_name: admin-pro
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=adminpro
      - DB_USERNAME=adminpro
      - DB_PASSWORD=adminpro123
    depends_on:
      - mysql
    volumes:
      - ./upload:/app/upload
      - ./logs:/app/logs

  mysql:
    image: mysql:8.0
    container_name: admin-pro-mysql
    environment:
      - MYSQL_ROOT_PASSWORD=root123
      - MYSQL_DATABASE=adminpro
      - MYSQL_USER=adminpro
      - MYSQL_PASSWORD=adminpro123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

运行：

```bash
docker-compose up -d
```

### 生产环境部署

#### 后端部署

1. **构建 JAR 包**
   ```bash
   mvn clean package -DskipTests
   ```

2. **运行 JAR**
   ```bash
   java -jar -Xms512m -Xmx1024m \
     -Dspring.profiles.active=prod \
     adminpro-web/target/adminpro-web.jar
   ```

3. **使用 systemd 管理（Linux）**
   
   创建 `/etc/systemd/system/admin-pro.service`：
   ```ini
   [Unit]
   Description=Admin Pro Application
   After=network.target
   
   [Service]
   Type=simple
   User=adminpro
   WorkingDirectory=/opt/admin-pro
   ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m \
     -Dspring.profiles.active=prod \
     /opt/admin-pro/adminpro-web.jar
   Restart=always
   RestartSec=10
   
   [Install]
   WantedBy=multi-user.target
   ```
   
   启动服务：
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable admin-pro
   sudo systemctl start admin-pro
   ```

#### 前端部署

1. **构建生产版本**
   ```bash
   cd frontend
   pnpm build
   ```

2. **部署到 Nginx**
   
   配置 Nginx：
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       root /var/www/admin-pro/dist;
       index index.html;
       
       location / {
           try_files $uri $uri/ /index.html;
       }
       
       location /api {
           proxy_pass http://localhost:8080/adminpro;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

3. **使用 PM2 管理（Node.js 环境）**
   ```bash
   npm install -g pm2
   pm2 serve /var/www/admin-pro/dist 3000 --spa
   ```

## 开发指南

### 本地开发

#### 后端开发

1. **IDE 配置**
   - 推荐使用 IntelliJ IDEA 或 Eclipse
   - 配置 JDK 21
   - 配置 Maven 3.9+
   - 安装 Lombok 插件

2. **数据库初始化**
   ```bash
   # 创建数据库
   CREATE DATABASE adminpro DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   # 首次启动会自动执行 Liquibase 变更日志，创建表结构
   ```

3. **运行配置**
   - 主类：`com.adminpro.Application`
   - 配置文件：`application-dev.yml`
   - 默认端口：`8080`
   - 上下文路径：`/adminpro`

4. **调试技巧**
   - 使用 Spring Boot DevTools 热重载
   - 查看日志：`logs/adminpro.log`
   - 查看错误日志：`logs/adminpro-error.log`
   - 使用 Swagger 测试 API：`http://localhost:8080/adminpro/swagger-ui.html`

#### 前端开发

1. **开发环境**
   ```bash
   cd frontend
   pnpm install
   pnpm dev
   ```

2. **开发服务器**
   - 默认端口：`3000` 或 `5173`（Vite）
   - 代理配置：自动代理 `/api` 到后端
   - 热重载：支持代码修改自动刷新

3. **代码检查**
   ```bash
   # ESLint 检查
   pnpm lint
   
   # 自动修复
   pnpm lint:fix
   
   # TypeScript 类型检查
   pnpm type-check
   ```

4. **调试技巧**
   - 使用 React DevTools
   - 使用浏览器开发者工具
   - 查看网络请求（Axios 拦截器已配置）

### 开发规范

#### 代码规范

- **后端**
  - 遵循阿里巴巴 Java 开发规范
  - 使用 Lombok 简化代码
  - 统一异常处理
  - 统一返回格式（R<T>）
  - 使用 @SysLog 记录操作日志

- **前端**
  - 使用 ESLint + TypeScript 进行代码检查
  - 组件采用函数式组件 + Hooks
  - 统一使用 pnpm 作为包管理器
  - 使用 React Hook Form + Zod 进行表单验证
  - 使用 Zustand 进行状态管理

#### 命名规范

- **后端**
  - 类名：PascalCase（如 `UserController`）
  - 方法名：camelCase（如 `getUserList`）
  - 常量：UPPER_SNAKE_CASE（如 `MAX_SIZE`）
  - 包名：小写，点分隔（如 `com.adminpro.web`）

- **前端**
  - 组件文件：PascalCase（如 `UserList.tsx`）
  - 工具函数：camelCase（如 `formatDate`）
  - 常量：UPPER_SNAKE_CASE（如 `API_BASE_URL`）
  - CSS 类名：kebab-case（如 `user-list-container`）

#### 文件组织

- **后端**
  - 按模块组织（rbac、tools 等）
  - Controller、Service、DAO 分层清晰
  - VO、Entity 分离

- **前端**
  - 按功能模块组织（pages 目录）
  - 公共组件放在 components
  - API 接口统一在 api 目录
  - 类型定义统一在 types 目录

### Git 提交规范

#### 提交消息格式

Git 提交消息采用约定式提交（Conventional Commits）格式：

```
<type>(<scope>): <subject>

<body>

<footer>
```

**格式说明**：
- **type**: 提交类型（必需）
- **scope**: 影响范围（可选），如模块名、文件名等
- **subject**: 简短描述（必需），不超过 50 个字符
- **body**: 详细描述（可选），可以多行
- **footer**: 相关引用（可选），如 issue 编号

#### 提交类型

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(user): 添加用户头像上传功能` |
| `fix` | 修复问题 | `fix(auth): 修复登录时验证码验证失败的问题` |
| `docs` | 文档更新 | `docs: 更新 README 中的缓存配置说明` |
| `style` | 代码格式调整（不影响代码运行） | `style: 格式化代码，统一缩进为 4 个空格` |
| `refactor` | 代码重构（既不是新功能也不是修复） | `refactor(cache): 重构缓存管理器的初始化逻辑` |
| `perf` | 性能优化 | `perf(query): 优化用户列表查询性能，添加索引` |
| `test` | 测试相关 | `test(user): 添加用户服务的单元测试` |
| `chore` | 构建/工具相关 | `chore: 更新 .gitignore 文件` |
| `upgrade` | 依赖升级 | `upgrade: 升级 Spring Boot 版本到 3.5.6` |

#### 使用示例

**基础格式（推荐）**：
```bash
git commit -m "feat(user): 添加用户头像上传功能"
```

**带详细描述**：
```bash
git commit -m "feat(user): 添加用户头像上传功能

- 支持图片格式：jpg、png、gif
- 文件大小限制：2MB
- 自动生成缩略图
- 集成阿里云 OSS 存储

Closes #123"
```

**多行提交消息**：
```bash
git commit
# 在打开的编辑器中输入：
feat(role): 添加角色权限批量分配功能

支持通过 Excel 批量导入角色权限关系，提高配置效率。

主要变更：
- 新增批量导入接口
- 添加数据校验逻辑
- 支持导入结果反馈

Related to #456
```

#### 实际提交示例

```bash
# 新功能
git commit -m "feat(menu): 添加菜单拖拽排序功能"

# 修复问题
git commit -m "fix(login): 修复登录后 Session 失效的问题"

# 文档更新
git commit -m "docs: 更新 README 中的缓存配置说明"

# 代码重构
git commit -m "refactor(cache): 重构缓存实现，统一使用 CacheManager"

# 性能优化
git commit -m "perf(query): 优化用户列表查询，添加数据库索引"

# 依赖升级
git commit -m "upgrade: 升级 React 版本到 19.1.1"

# 构建相关
git commit -m "chore: 更新 Docker 构建配置"
```

#### 最佳实践

1. **提交类型使用小写**：`feat` 而不是 `Feat` 或 `FEAT`
2. **scope 使用括号**：`feat(user):` 而不是 `feat user:`
3. **subject 使用祈使句**：`添加功能` 而不是 `添加了功能` 或 `添加功能了`
4. **subject 首字母小写**：`fix: 修复问题` 而不是 `fix: 修复问题`
5. **subject 结尾不加句号**：`feat: 添加功能` 而不是 `feat: 添加功能。`
6. **一次提交只做一件事**：不要在一个提交中同时修复多个不相关的问题
7. **提交前检查**：使用 `git diff` 查看变更，确保提交的内容正确

#### 提交消息模板

可以在 `.gitmessage` 文件中创建模板：

```
<type>(<scope>): <subject>

# 为什么需要这个变更
# 解决了什么问题

# 主要变更内容
# - 变更点 1
# - 变更点 2

# 相关 Issue
# Closes #123
```

然后在 Git 配置中设置：
```bash
git config commit.template .gitmessage
```

### 代码生成

系统提供代码生成功能，支持：

- **前端代码生成**:
  - React + TypeScript 组件 (List.tsx, Form.tsx)
  - API 接口文件 (api.ts)
  - TypeScript 类型定义 (types.ts)

- **后端代码生成**:
  - Entity 实体类
  - Service 服务类
  - Controller 控制器
  - DAO 数据访问层
  - Mapper XML (MyBatis)
  - Repository 接口 (JPA)

访问代码生成器页面，选择数据表即可自动生成完整的 CRUD 代码。

### API 接口说明

#### 统一返回格式

所有 API 接口统一返回格式（使用 `R<T>` 类）：

**成功响应示例**：
```json
{
  "restCode": "200",
  "message": "操作成功",
  "success": true,
  "data": {
    "id": "123",
    "name": "用户名"
  },
  "errors": [],
  "errorsMap": {}
}
```

**失败响应示例**：
```json
{
  "restCode": "500",
  "message": "操作失败：用户名不能为空",
  "success": false,
  "data": null,
  "errors": [
    {
      "field": "username",
      "message": "用户名不能为空"
    }
  ],
  "errorsMap": {
    "username": "用户名不能为空"
  }
}
```

**字段说明**：
- `restCode`: 状态码（字符串类型，如 "200"、"500" 等）
- `message`: 提示信息
- `success`: 是否成功（true/false）
- `data`: 返回数据（泛型，可能为 null）
- `errors`: 错误列表（数组，包含字段和错误信息）
- `errorsMap`: 错误映射（对象，key 为字段名，value 为错误信息）

**常用状态码**：
- `"200"`: 操作成功
- `"401"`: 未授权（需要登录）
- `"403"`: 无权限访问
- `"500"`: 服务器内部错误

#### 主要 API 接口

**认证相关**
- `POST /rest/auth/login` - 用户登录
- `POST /rest/auth/logout` - 用户登出
- `POST /rest/auth/changePassword` - 修改密码
- `GET /rest/auth/captcha` - 获取验证码

**用户管理**
- `GET /admin/user/list` - 用户列表
- `GET /admin/user/detail/{id}` - 用户详情
- `POST /admin/user/create` - 创建用户
- `PATCH /admin/user/edit` - 更新用户
- `DELETE /admin/user/delete` - 删除用户

**角色管理**
- `GET /admin/role/list` - 角色列表
- `POST /admin/role/create` - 创建角色
- `PATCH /admin/role/edit` - 更新角色
- `DELETE /admin/role/delete` - 删除角色

**菜单管理**
- `GET /admin/menu/list` - 菜单列表
- `POST /admin/menu/create` - 创建菜单
- `PATCH /admin/menu/edit` - 更新菜单
- `DELETE /admin/menu/delete` - 删除菜单

**系统配置**
- `GET /admin/config/list` - 参数配置列表
- `GET /admin/dict/list` - 字典列表
- `GET /common/info` - 系统信息

更多 API 接口请参考 Swagger 文档。

### 最佳实践

#### 后端开发

1. **使用统一异常处理**
   - 业务异常使用 `BusinessException`
   - 系统异常自动记录日志

2. **使用 AOP 记录日志**
   - 使用 `@SysLog` 注解记录操作日志
   - 自动记录请求参数和返回结果

3. **使用缓存提升性能**
   - 使用 `@Cacheable` 缓存查询结果
   - 使用 `@CacheEvict` 清除缓存

4. **使用事务管理**
   - 使用 `@Transactional` 管理事务
   - 注意事务传播行为

#### 前端开发

1. **使用 TypeScript**
   - 定义明确的类型
   - 使用接口定义 API 返回类型

2. **使用 React Hooks**
   - 使用 `useState` 管理状态
   - 使用 `useEffect` 处理副作用
   - 使用自定义 Hooks 复用逻辑

3. **使用 Zustand 管理全局状态**
   - 用户信息存储在 store
   - 避免过度使用全局状态

4. **使用 React Hook Form**
   - 表单验证使用 Zod
   - 减少不必要的重渲染

5. **错误处理**
   - 使用 ErrorBoundary 捕获错误
   - 统一处理 API 错误

### 性能优化

#### 后端优化

1. **数据库优化**
   - 合理使用索引
   - 避免 N+1 查询
   - 使用分页查询
   - 使用连接池

2. **缓存策略**
   - 热点数据使用缓存
   - 设置合理的过期时间
   - 使用缓存预热

3. **异步处理**
   - 使用 `@Async` 处理耗时操作
   - 使用消息队列处理异步任务

#### 前端优化

1. **代码分割**
   - 使用路由懒加载
   - 按需加载组件

2. **资源优化**
   - 图片懒加载
   - 使用 CDN 加速
   - 压缩静态资源

3. **请求优化**
   - 合并请求
   - 使用防抖和节流
   - 缓存 API 响应

### 安全性

#### 后端安全

1. **认证授权**
   - 使用 Spring Security
   - JWT Token 或 Session 管理
   - 密码加密存储

2. **SQL 注入防护**
   - 使用参数化查询
   - 避免拼接 SQL

3. **XSS 防护**
   - 输入验证和过滤
   - 输出转义

4. **CSRF 防护**
   - 使用 CSRF Token
   - 验证请求来源

#### 前端安全

1. **XSS 防护**
   - 使用 React 的自动转义
   - 避免使用 `dangerouslySetInnerHTML`

2. **敏感信息保护**
   - 不在前端存储敏感信息
   - 使用 HTTPS 传输

3. **权限控制**
   - 路由权限控制
   - 按钮权限控制

## 升级说明

### 主要升级内容

- ✅ **Spring Boot 2.7.18 → 3.5.6**
  - 迁移到 Jakarta EE (javax.* → jakarta.*)
  - Spring Security 6.x API 更新
  - 移除对 EhCache 2.x 的直接支持

- ✅ **Java 8 → Java 21**
  - 使用现代 Java 特性
  - 更好的性能和安全性

- ✅ **代码生成引擎迁移**
  - Velocity → FreeMarker
  - 统一使用 FreeMarker 模板引擎

- ✅ **对象映射库升级**
  - Orika → ModelMapper
  - 完全支持 Java 21，无需 JVM 参数

- ✅ **API 文档升级**
  - Springfox → springdoc-openapi
  - 支持 OpenAPI 3.0 规范

- ✅ **JSON 处理升级**
  - Fastjson → Gson
  - 更好的安全性和兼容性

- ✅ **缓存系统优化**
  - EhCache 2.x → Spring Cache (Simple/Redis/JCache)
  - 支持三种缓存类型：simple（内存）、redis（分布式）、jcache（EhCache 3.x）
  - 更好的 Spring Boot 集成

- ✅ **依赖升级**
  - MySQL Connector: 5.1.38 → 8.3.0
  - 其他第三方库全面升级

## 常见问题

### 1. Java 版本问题

**错误**: `无效的目标发行版: 21`

**解决方案**:
- 确保已安装 JDK 21
- 设置 `JAVA_HOME` 环境变量指向 JDK 21
- 在 IDE 中配置项目使用 Java 21

### 2. 数据库连接失败

检查数据库配置是否正确，确保数据库服务已启动。

**注意**: MySQL 8.0+ 需要使用新的驱动类 `com.mysql.cj.jdbc.Driver`

### 3. 缓存配置问题

**错误**: `No enum constant org.springframework.boot.autoconfigure.cache.CacheType.ehcache`

**解决方案**: 
- Spring Boot 3.x 不再支持 `spring.cache.type=ehcache`
- 使用 `spring.cache.type=simple`、`spring.cache.type=redis` 或 `spring.cache.type=jcache`
- 如果使用 EhCache，请使用 `spring.cache.type=jcache` 并配置 `ehcache.xml` 文件

### 4. 前端代理错误

检查 `vite.config.ts` 中的代理配置，确保后端服务地址正确。

### 5. 端口被占用

修改 `application.yml` 中的 `server.port` 或 `vite.config.ts` 中的 `server.port`。

### 6. 模块系统错误

**错误**: `module java.base does not "opens java.lang"`

**解决方案**: 
- 已迁移到 ModelMapper，不再需要 JVM 参数
- 如果仍有问题，检查是否有其他依赖需要模块访问权限

## 技术栈迁移说明

### 从旧版本升级

如果您正在从旧版本升级，请注意以下变更：

1. **Java 版本**: 必须升级到 JDK 21
2. **Spring Boot**: 已升级到 3.5.6，需要适配 Jakarta EE
3. **缓存配置**: 将 `spring.cache.type=ehcache` 改为 `spring.cache.type=simple`、`redis` 或 `jcache`
4. **API 文档**: Swagger UI 路径从 `/swagger-ui.html` 改为 `/swagger-ui.html` (springdoc-openapi)
5. **代码生成**: 模板已从 Velocity 迁移到 FreeMarker

### 兼容性说明

- ✅ 完全兼容 MySQL 8.0+
- ✅ 支持 Java 21 LTS
- ✅ 支持 Spring Boot 3.x 生态
- ⚠️ 不再支持 Java 8/11
- ⚠️ 不再支持 EhCache 2.x，如需使用 EhCache 请使用 `spring.cache.type=jcache` 配合 EhCache 3.x

## 更新日志

### v1.0.0 (2025-11-20)

- 🎉 升级到 Spring Boot 3.5.6
- 🎉 升级到 Java 21
- 🎉 代码生成引擎迁移到 FreeMarker
- 🎉 对象映射库迁移到 ModelMapper
- 🎉 API 文档升级到 springdoc-openapi
- 🎉 JSON 处理迁移到 Gson
- 🎉 缓存系统优化
- 🔧 修复多个编译和运行时错误
- 📝 更新文档和配置

## 许可证

查看 [LICENSE](LICENSE) 文件了解详情。

## 贡献

欢迎提交 Issue 和 Pull Request。

## 功能规划

基于现有功能模块，以下是后续可以扩展的功能方向，按优先级和重要性分类：

### 🔥 高优先级功能

#### 1. 消息通知系统
- **站内消息**
  - 消息列表、消息详情
  - 消息已读/未读状态
  - 消息分类（系统通知、业务通知等）
  - 消息推送提醒
- **邮件通知**
  - 邮件模板管理
  - 邮件发送记录
  - 支持 HTML 邮件
- **短信通知**
  - 短信服务集成（阿里云、腾讯云等）
  - 短信模板管理
  - 短信发送记录
- **消息中心**
  - 统一消息入口
  - 消息统计
  - 消息设置（免打扰时段等）

#### 2. 文件管理系统
- **文件上传/下载**
  - 支持多文件上传
  - 文件类型限制
  - 文件大小限制
  - 上传进度显示
- **文件预览**
  - 图片预览（支持缩放、旋转）
  - PDF 预览
  - Office 文档预览（Word、Excel、PPT）
  - 视频/音频预览
- **文件管理**
  - 文件分类管理
  - 文件搜索
  - 文件重命名、移动、删除
  - 文件分享（生成分享链接）
- **云存储集成**
  - 阿里云 OSS
  - 腾讯云 COS
  - 七牛云
  - 本地存储
  - 存储策略配置（自动切换）

#### 3. 数据权限增强
- **数据范围权限**
  - 全部数据权限
  - 本部门数据权限
  - 本部门及下级部门数据权限
  - 仅本人数据权限
  - 自定义数据权限
- **字段级权限控制**
  - 字段可见性控制
  - 字段编辑权限控制
  - 敏感字段脱敏
- **行级数据权限**
  - 基于部门的数据过滤
  - 基于角色的数据过滤
  - 动态 SQL 权限过滤

### 📊 中优先级功能

#### 4. 工作流引擎
- **流程设计器**
  - 可视化流程设计
  - 流程节点配置（审批人、抄送人等）
  - 流程条件分支
  - 流程版本管理
- **流程实例管理**
  - 流程发起
  - 流程审批
  - 流程撤回
  - 流程转办
  - 流程催办
- **流程监控**
  - 流程实例列表
  - 流程执行情况统计
  - 流程耗时分析
  - 流程异常处理

#### 5. 多租户支持
- **租户管理**
  - 租户列表
  - 租户新增/编辑/删除
  - 租户状态管理
  - 租户配置
- **数据隔离**
  - 数据库级隔离
  - Schema 级隔离
  - 行级隔离（tenant_id）
- **租户配置**
  - 租户个性化配置
  - 租户资源限制
  - 租户计费管理

#### 6. 国际化支持
- **多语言切换**
  - 前端多语言支持
  - 后端多语言支持
  - 语言包管理
  - 动态语言切换
- **时区支持**
  - 时区配置
  - 时间显示格式化
  - 时区自动识别
- **语言包管理**
  - 语言包导入/导出
  - 语言包在线编辑
  - 语言包版本管理

#### 7. 主题定制
- **主题切换**
  - 亮色/暗色主题
  - 自定义主题色
  - 主题预览
  - 主题保存
- **布局配置**
  - 侧边栏布局
  - 顶部导航布局
  - 混合布局
  - 布局自定义
- **个性化设置**
  - 用户偏好设置
  - 界面元素显示/隐藏
  - 快捷操作配置

### 🚀 扩展功能

#### 8. 报表系统
- **报表设计器**
  - 可视化报表设计
  - 数据源配置
  - 图表类型（柱状图、折线图、饼图等）
  - 报表参数配置
- **数据可视化**
  - 仪表盘
  - 数据大屏
  - 图表联动
  - 数据钻取
- **报表管理**
  - 报表列表
  - 报表分享
  - 报表导出（PDF、Excel）
  - 报表定时生成

#### 9. 操作日志增强
- **操作回放**
  - 操作记录回放
  - 操作步骤可视化
  - 操作对比
- **日志分析**
  - 操作统计
  - 异常操作识别
  - 用户行为分析
- **日志导出**
  - 日志批量导出
  - 日志归档
  - 日志清理策略

#### 10. 数据导入导出增强
- **批量操作**
  - 批量导入
  - 批量导出
  - 导入模板下载
  - 导入数据校验
  - 导入结果反馈
- **高级导出**
  - 自定义导出字段
  - 导出格式选择（Excel、PDF、CSV）
  - 导出数据过滤
  - 导出任务队列

#### 11. API 网关集成
- **API 管理**
  - API 注册
  - API 版本管理
  - API 文档生成
  - API 测试
- **流量控制**
  - 限流配置
  - 熔断降级
  - 负载均衡
- **安全控制**
  - API 鉴权
  - 签名验证
  - IP 白名单

#### 12. 缓存优化
- **Redis 集群**
  - Redis 集群配置
  - 缓存策略优化
  - 缓存预热
  - 缓存穿透/击穿/雪崩防护
- **缓存管理**
  - 缓存监控
  - 缓存清理
  - 缓存统计

#### 13. 搜索引擎集成
- **Elasticsearch 集成**
  - 全文搜索
  - 高级搜索（多条件、排序）
  - 搜索高亮
  - 搜索建议
- **搜索管理**
  - 索引管理
  - 搜索配置
  - 搜索统计

#### 14. 其他增强功能
- **在线用户管理**
  - 在线用户列表
  - 强制下线
  - 用户会话管理
- **系统监控**
  - 系统性能监控
  - 数据库监控
  - 接口性能监控
  - 告警通知
- **备份恢复**
  - 数据备份
  - 数据恢复
  - 备份策略配置
- **系统升级**
  - 在线升级
  - 版本管理
  - 升级回滚

### 实施建议

1. **优先级排序**：根据业务需求，优先实施高优先级功能
2. **迭代开发**：采用敏捷开发方式，分阶段实施
3. **技术选型**：选择成熟稳定的技术方案
4. **文档完善**：每个功能模块都要有完整的文档
5. **测试覆盖**：确保新功能的测试覆盖率

## 联系方式

如有问题或建议，请通过 Issue 反馈。

