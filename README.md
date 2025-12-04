# Admin Pro 管理系统

<div align="center">
  <h3>企业级权限管理系统</h3>
  <p>基于 Spring Boot + React 的现代化管理平台</p>
  <p>
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=spring" alt="Spring Boot Version" />
    <img src="https://img.shields.io/badge/React-19.x-blue?logo=react" alt="React Version" />
    <img src="https://img.shields.io/badge/TypeScript-5.9.x-blue?logo=typescript" alt="TypeScript Version" />
    <img src="https://img.shields.io/badge/Java-21-orange?logo=java" alt="Java Version" />
    <img src="https://img.shields.io/badge/MySQL-8.0+-blue?logo=mysql" alt="MySQL Version" />
  </p>
</div>

## 项目简介

Admin Pro 是一个前后端分离的企业级权限管理系统，提供完整的用户管理、角色权限、菜单管理、组织架构等核心功能。系统采用现代化的技术栈，具有良好的扩展性和可维护性。

本项目包含三个主要部分：

1. **后端 API**: 基于 Spring Boot 的 RESTful API 服务
2. **管理后台 (Frontend)**: 基于 React + Ant Design 的单页应用 (SPA)，采用**混合主题**设计（深色侧边栏 + 浅色内容区）
3. **门户网站 (Portal)**: 基于 React 的多语言展示型官网，采用**高级深色主题**设计

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
- **任务调度**: Quartz
- **其他**: 
  - EasyPOI (Excel导入导出)
  - Kaptcha (验证码)
  - Oshi (系统信息监控)

### 前端技术 (Frontend - 管理后台)

- **框架**: React 19.1.1
- **构建工具**: Vite 7.1.7
- **语言**: TypeScript 5.9.3
- **UI组件库**: Ant Design 5.27.4
- **状态管理**: Zustand 5.0.8
- **路由**: React Router DOM 7.9.4
- **HTTP客户端**: Axios 1.12.2
- **表单处理**: React Hook Form + Zod
- **包管理器**: pnpm 9.0.0

### 门户网站 (Portal)

- **框架**: React 19.2.0
- **构建工具**: Vite 7.2.4
- **UI组件库**: Ant Design 6.0.1
- **国际化**: i18next + react-i18next

## 项目结构

```
admin-pro/
├── adminpro-system/              # 后端：公共业务模块
│   ├── src/main/java/com/adminpro/
│   │   ├── api/                  # APK相关API
│   │   ├── config/               # 配置类
│   │   │   ├── ApiWebSecurityConfig.java
│   │   │   ├── CaptchaConfig.java
│   │   │   ├── EhcacheConfig.java
│   │   │   ├── ErrorBasicController.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── RedisCacheConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SessionConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── ...
│   │   ├── framework/            # 框架核心
│   │   │   ├── batchjob/         # 定时任务
│   │   │   ├── cache/             # 缓存
│   │   │   ├── common/            # 通用工具
│   │   │   ├── exceptions/        # 异常定义
│   │   │   ├── filters/           # 过滤器
│   │   │   ├── security/          # 安全相关
│   │   │   └── manager/           # 管理器
│   │   ├── rbac/                 # RBAC权限模块
│   │   │   ├── api/               # 权限API辅助类
│   │   │   ├── common/            # 公共常量
│   │   │   ├── domains/           # 领域模型
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   └── vo/            # 视图对象
│   │   │   ├── encrypt/           # 加密工具
│   │   │   └── enums/             # 枚举类
│   │   ├── tools/                # 工具模块
│   │   │   ├── api/               # 工具API（OSS、支付等）
│   │   │   ├── domains/           # 领域模型
│   │   │   ├── gen/               # 代码生成
│   │   │   ├── lock/              # 分布式锁
│   │   │   ├── payment/           # 支付（支付宝、微信）
│   │   │   ├── ueditor/           # 富文本编辑器
│   │   │   └── wx/                # 微信相关
│   │   └── web/                   # Web控制器
│   │       ├── rbac/              # 权限相关控制器
│   │       │   ├── AuthController.java
│   │       │   ├── UserController.java
│   │       │   ├── RoleController.java
│   │       │   ├── MenuController.java
│   │       │   ├── DeptController.java
│   │       │   └── ...
│   │       └── tools/             # 工具相关控制器
│   │           ├── CodeGeneratorController.java
│   │           ├── JobController.java
│   │           ├── ConfigController.java
│   │           └── ...
│   └── src/main/resources/
│       ├── changelog/            # Liquibase变更日志
│       └── templates/            # FreeMarker模板
├── adminpro-core/                # 后端：核心基础模块
│   └── src/main/java/com/adminpro/core/
│       ├── aspect/               # AOP切面
│       ├── base/                 # 基础类
│       ├── config/               # 核心配置
│       ├── exceptions/           # 异常定义
│       ├── jdbc/                 # JDBC封装
│       └── tools/                # 工具
├── adminpro-web/                 # 后端：Web启动模块
│   ├── src/main/java/com/adminpro/
│   │   └── Application.java      # 启动类
│   └── src/main/resources/
│       ├── application.yml       # 应用配置
│       ├── application-dev.yml   # 开发环境配置
│       ├── application-prod.yml  # 生产环境配置
│       └── logback/              # 日志配置
├── frontend/                     # 前端：管理后台项目
│   ├── src/
│   │   ├── api/                   # API接口
│   │   ├── components/            # 公共组件
│   │   ├── pages/                 # 页面组件
│   │   │   ├── Layout.tsx         # 主布局
│   │   │   ├── Login/             # 登录页面
│   │   │   ├── User/              # 用户管理
│   │   │   ├── Role/              # 角色管理
│   │   │   ├── Menu/              # 菜单管理
│   │   │   ├── Dept/              # 部门管理
│   │   │   └── ...
│   │   ├── router/                # 路由配置
│   │   ├── stores/                # 状态管理
│   │   ├── types/                 # TypeScript类型定义
│   │   └── utils/                 # 工具函数
│   ├── package.json
│   └── vite.config.ts
├── portal/                       # 前端：门户网站项目
│   ├── src/
│   │   ├── components/            # 门户组件
│   │   ├── locales/               # 国际化资源
│   │   └── ...
│   └── package.json
├── docker/                       # Docker配置
│   ├── backend/                   # 后端Docker配置
│   ├── frontend/                  # 前端Docker配置
│   └── start.sh                  # 启动脚本
├── pom.xml                       # Maven父POM
├── docker-compose.yml            # Docker Compose配置
└── README.md                     # 项目文档
```

## 环境要求

### 后端环境

- **JDK 21+** (必需)
- Maven 3.9+
- MySQL 8.0+

### 前端环境

- Node.js 18+
- pnpm 9.0.0+ (推荐)

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

修改 `adminpro-web/src/main/resources/application.yml` 中的数据库配置。

系统使用 Liquibase 自动管理数据库版本，首次启动会自动创建表结构。

### 3. 启动后端

```bash
cd admin-pro
mvn clean install
cd adminpro-web
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/adminpro` 启动。

### 4. 启动管理后台 (Frontend)

```bash
cd frontend
pnpm install
pnpm dev
```

管理后台将在 `http://localhost:3000` 启动。

### 5. 启动门户网站 (Portal)

```bash
cd portal
pnpm install
pnpm dev
```

门户网站将在 `http://localhost:5173` (或其他可用端口) 启动。

### 6. 访问系统

- **管理后台**: http://localhost:3000
- **门户网站**: http://localhost:5173
- **后端API**: http://localhost:8080/adminpro
- **API文档**: http://localhost:8080/adminpro/swagger-ui.html

### 7. 默认账号

首次启动后，系统会自动创建默认管理员账号（如果数据库为空）：
- **用户名**: superadmin
- **密码**: password$1（请首次登录后修改）

**注意**: 生产环境部署前，请务必修改默认密码！

## 功能模块

### 管理后台核心功能

- ✅ **用户认证**: 登录、验证码、Session管理
- ✅ **权限管理**: 用户、角色、菜单、部门、岗位
- ✅ **系统配置**: 参数配置、字典管理
- ✅ **监控运维**: 系统监控、日志查看、在线用户
- ✅ **开发工具**: 代码生成器、Swagger文档

### 门户网站功能

- ✅ **现代化设计**: 响应式布局，高级深色主题
- ✅ **多语言支持**: 中英文无缝切换
- ✅ **产品展示**: 核心特性、技术栈展示

## 部署

详细部署说明请参考 [DOCKER_DEPLOY.md](DOCKER_DEPLOY.md)。

### Docker Compose 一键部署

```bash
docker-compose up -d
```

这将同时启动 MySQL、后端服务和前端 Nginx 服务。

## 开发规范

### 代码规范

- **后端**
  - 遵循阿里巴巴 Java 开发规范
  - 使用 Lombok 简化代码
  - 统一异常处理（GlobalExceptionHandler）
  - 统一返回格式（R<T>）
  - 使用 @SysLog 记录操作日志
  - 添加空值检查，避免 NPE 异常
  - 使用依赖注入替代 getInstance() 静态方法
  - 使用 logger 记录日志，禁止使用 printStackTrace
  - 提取重复代码为公共方法
  - 使用常量替代硬编码字符串
  - 优化数据库查询，避免 N+1 问题

- **前端**
  - 使用 ESLint + TypeScript 进行代码检查
  - 组件采用函数式组件 + Hooks
  - 统一使用 pnpm 作为包管理器
  - 使用 React Hook Form + Zod 进行表单验证
  - 使用 Zustand 进行状态管理

### 命名规范

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

## API 接口说明

### 统一返回格式

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

### 主要 API 接口

**认证相关**
- `POST /auth/login` - 用户登录
- `POST /auth/logout` - 用户登出
- `POST /auth/changePassword` - 修改密码
- `GET /auth/captcha` - 获取验证码

**用户管理**
- `POST /admin/user/list` - 用户列表（分页）
- `GET /admin/user/detail/{userDomain}/{userId}` - 用户详情
- `POST /admin/user` - 创建用户
- `PATCH /admin/user` - 更新用户
- `DELETE /admin/user/delete` - 删除用户
- `PATCH /admin/user/active/{userDomain}/{userId}` - 激活用户
- `PATCH /admin/user/inactive/{userDomain}/{userId}` - 停用用户
- `PATCH /admin/user/resetpwd` - 重置密码
- `GET /admin/user/prepare` - 获取准备数据（部门、角色、岗位）

更多 API 接口请参考 Swagger 文档。

## 性能优化

### 后端优化

1. **数据库优化**
   - 合理使用索引
   - 避免 N+1 查询（已优化：使用批量查询）
   - 使用分页查询
   - 使用连接池
   - 批量操作优化（批量删除、批量更新）

2. **缓存策略**
   - 热点数据使用缓存
   - 设置合理的过期时间
   - 使用缓存预热

3. **异步处理**
   - 使用 `@Async` 处理耗时操作
   - 使用消息队列处理异步任务

### 前端优化

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

## 安全性

### 后端安全

1. **认证授权**
   - 使用 Spring Security
   - Session 管理
   - 密码加密存储（SHA256）

2. **SQL 注入防护**
   - 使用参数化查询
   - 避免拼接 SQL

3. **XSS 防护**
   - 输入验证和过滤
   - 输出转义

4. **CSRF 防护**
   - 使用 CSRF Token
   - 验证请求来源

### 前端安全

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

## 更新日志

### v1.0.1 (2025-01-XX)

- ✨ **代码质量优化**
  - 添加空值检查，避免 NPE 异常
  - 修复 N+1 查询问题，使用批量查询优化性能
  - 将 getInstance() 改为依赖注入，提升代码可测试性
  - 使用 logger 替代 printStackTrace，规范日志记录
  - 提取重复代码为公共方法，提升代码复用性
  - 添加参数验证注解（@Valid、@NotNull），增强数据校验
  - 完善全局异常处理器，统一错误响应格式
  - 优化批量操作性能，使用批量 SQL 删除
  - 提取硬编码字符串为常量，提升可维护性
  - 添加详细日志记录，便于问题排查
  - 使用 try-with-resources 优化资源管理
- 🔧 修复 UserDao 批量删除方法的编译错误
- 📝 完善代码注释和文档

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
