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
- **缓存**: Spring Cache (Simple/Redis)
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
│   ├── api/                   # API控制器
│   ├── config/                # 配置类
│   ├── framework/             # 框架核心
│   │   ├── batchjob/          # 批处理任务
│   │   ├── cache/             # 缓存
│   │   ├── common/            # 通用工具
│   │   ├── exceptions/        # 异常处理
│   │   ├── filters/           # 过滤器
│   │   ├── security/          # 安全相关
│   │   └── manager/           # 管理器
│   ├── rbac/                  # RBAC权限模块
│   │   ├── api/               # 权限API
│   │   ├── domains/           # 领域模型
│   │   ├── encrypt/          # 加密工具
│   │   └── enums/            # 枚举类
│   ├── tools/                 # 工具模块
│   │   ├── api/               # 工具API
│   │   ├── domains/           # 领域模型
│   │   ├── gen/               # 代码生成
│   │   ├── lock/              # 分布式锁
│   │   ├── payment/           # 支付
│   │   ├── ueditor/           # 富文本编辑器
│   │   └── wx/                # 微信相关
│   └── web/                   # Web控制器
├── adminpro-core/             # 核心业务模块
│   └── src/main/java/com/adminpro/core/
├── adminpro-web/              # Web启动模块
│   ├── src/main/java/com/adminpro/Application.java
│   └── src/main/resources/
│       ├── application.yml    # 应用配置
│       ├── changelog/         # Liquibase变更日志
│       └── logback/           # 日志配置
├── frontend/                  # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 公共组件
│   │   ├── pages/             # 页面组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── types/             # 类型定义
│   │   └── utils/             # 工具函数
│   ├── package.json
│   └── vite.config.ts
├── docker/                    # Docker配置
├── stack/                     # 部署配置
├── pom.xml                    # Maven父POM
└── Dockerfile                 # Docker构建文件
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

- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080/adminpro
- API文档: http://localhost:8080/adminpro/swagger-ui.html
- API JSON: http://localhost:8080/adminpro/v3/api-docs

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

```yaml
spring:
  cache:
    type: simple  # 可选值: simple, redis
    # 如果使用 Redis，取消注释以下配置
    # type: redis
```

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

```bash
# 构建镜像
docker build -t admin-pro:latest .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_HOST=your_db_host \
  -e DB_USERNAME=your_username \
  -e DB_PASSWORD=your_password \
  admin-pro:latest
```

## 开发规范

### 代码规范

- 后端遵循阿里巴巴 Java 开发规范
- 前端使用 ESLint + TypeScript 进行代码检查
- 组件采用函数式组件 + Hooks
- 统一使用 pnpm 作为包管理器
- 代码生成使用 FreeMarker 模板引擎

### Git 提交规范

- `feat`: 新功能
- `fix`: 修复问题
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具相关
- `upgrade`: 依赖升级

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
  - EhCache 2.x → Spring Cache (Simple/Redis)
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
- 使用 `spring.cache.type=simple` 或 `spring.cache.type=redis`

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
3. **缓存配置**: 将 `spring.cache.type=ehcache` 改为 `spring.cache.type=simple` 或 `redis`
4. **API 文档**: Swagger UI 路径从 `/swagger-ui.html` 改为 `/swagger-ui.html` (springdoc-openapi)
5. **代码生成**: 模板已从 Velocity 迁移到 FreeMarker

### 兼容性说明

- ✅ 完全兼容 MySQL 8.0+
- ✅ 支持 Java 21 LTS
- ✅ 支持 Spring Boot 3.x 生态
- ⚠️ 不再支持 Java 8/11
- ⚠️ 不再支持 EhCache 2.x（可通过 `app.cache.ehcache.enabled=true` 启用，但不推荐）

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

## 联系方式

如有问题或建议，请通过 Issue 反馈。

