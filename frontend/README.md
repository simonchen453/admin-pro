# Admin Pro 管理系统

<div align="center">
  <img src="/public/logo-large.svg" alt="Admin Pro Logo" width="128" height="128" />
  <h3>Admin Pro 企业级管理系统</h3>
  <p>基于 Spring Boot + React 的现代化管理平台</p>
  <p>
    <img src="https://img.shields.io/badge/React-19.1.1-blue?logo=react" alt="React Version" />
    <img src="https://img.shields.io/badge/TypeScript-5.9.3-blue?logo=typescript" alt="TypeScript Version" />
    <img src="https://img.shields.io/badge/Ant%20Design-5.27.4-blue?logo=ant-design" alt="Ant Design Version" />
    <img src="https://img.shields.io/badge/Vite-7.1.7-646CFF?logo=vite" alt="Vite Version" />
  </p>
</div>

基于 React + TypeScript + Vite + Ant Design 构建的现代化企业级管理系统，提供完整的用户管理、权限控制、菜单管理等核心功能。

## 技术栈

- **前端框架**: React 19
- **构建工具**: Vite 7
- **语言**: TypeScript 5.9
- **UI组件库**: Ant Design 5.27
- **状态管理**: Zustand
- **路由**: React Router DOM 7
- **HTTP客户端**: Axios
- **表单处理**: React Hook Form + Zod
- **样式**: CSS + Ant Design
- **包管理器**: pnpm

## 项目结构

```
src/
├── api/                    # API请求相关
│   ├── request.ts          # Axios配置和拦截器
│   ├── auth.ts             # 认证相关API
│   ├── user.ts             # 用户管理API
│   ├── role.ts             # 角色管理API
│   ├── menu.ts             # 菜单管理API
│   ├── dept.ts             # 部门管理API
│   ├── post.ts             # 岗位管理API
│   ├── config.ts           # 参数配置API
│   ├── dict.ts             # 字典管理API
│   ├── domain.ts           # 用户域管理API
│   ├── domainEnv.ts        # 用户域环境配置API
│   ├── job.ts              # 定时任务API
│   ├── session.ts          # 会话管理API
│   ├── server.ts           # 服务器信息API
│   ├── syslog.ts           # 系统日志API
│   ├── audit.ts            # 审计日志API
│   ├── generator.ts        # 代码生成器API
│   └── common.ts           # 通用API（系统信息等）
├── components/             # 公共组件
│   ├── Captcha.tsx         # 验证码组件
│   ├── ErrorBoundary.tsx   # 错误边界
│   ├── ProtectedRoute.tsx  # 受保护路由
│   └── PublicRoute.tsx     # 公开路由
├── pages/                  # 页面组件
│   ├── Layout.tsx          # 主布局组件
│   ├── Login/              # 登录页面
│   ├── Home.tsx            # 首页
│   ├── ChangePassword/     # 修改密码
│   ├── User/               # 用户管理
│   ├── Role/               # 角色管理
│   ├── Menu/               # 菜单管理
│   ├── Dept/               # 部门管理
│   ├── Post/               # 岗位管理
│   ├── Config/             # 参数配置
│   ├── Dict/               # 字典管理
│   ├── Domain/             # 用户域管理
│   ├── DomainEnv/          # 用户域环境配置
│   ├── Job/                # 定时任务
│   ├── Session/            # 会话管理
│   ├── Server/             # 服务器信息
│   ├── SysLog/             # 系统日志
│   ├── AuditLog/           # 审计日志
│   ├── Generator/          # 代码生成器
│   ├── Swagger/            # API文档
│   ├── NoPermission.tsx    # 无权限页面
│   └── NotFound.tsx        # 404页面
├── router/                 # 路由配置
│   └── AppRouter.tsx       # 路由定义
├── stores/                 # 状态管理
│   └── useUserStore.ts     # 用户状态
├── types/                  # TypeScript类型定义
│   └── index.ts            # 类型导出
├── utils/                  # 工具函数
├── config/                 # 配置文件
│   └── env.ts              # 环境配置
├── App.tsx                 # 根组件
└── main.tsx                # 入口文件
```

## 开发指南

### 环境要求

- Node.js >= 18
- pnpm >= 9.0.0 (推荐) 或 npm/yarn

### 安装依赖

```bash
pnpm install
```

### 启动开发服务器

```bash
pnpm dev
```

开发服务器将在 `http://localhost:5173` 启动

### 构建生产版本

```bash
pnpm build
```

### 预览生产构建

```bash
pnpm preview
```

### 代码检查

```bash
# 检查代码
pnpm lint

# 自动修复
pnpm lint:fix

# 类型检查
pnpm type-check
```

### 清理构建文件

```bash
pnpm clean
```

## 环境变量

创建 `.env` 文件并配置以下变量：

```env
# API基础路径
VITE_API_BASE=http://localhost:3000/api

# 应用标题
VITE_APP_TITLE=Admin Pro 管理系统
```

## 功能特性

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

- ✅ **权限管理**
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
  - 代码生成器
  - Swagger API 文档

### 技术特性

- ✅ 现代化技术栈（React 19 + TypeScript）
- ✅ TypeScript 类型安全
- ✅ 响应式布局设计
- ✅ 路由懒加载
- ✅ 状态管理（Zustand）
- ✅ API 请求封装和拦截
- ✅ 表单验证（React Hook Form + Zod）
- ✅ 错误边界处理
- ✅ 权限路由保护
- ✅ 系统信息动态展示

## 开发规范

### 代码规范

- 使用 TypeScript 进行类型检查
- 遵循 ESLint 代码规范
- 组件采用函数式组件 + Hooks
- 状态管理使用 Zustand
- 样式优先使用 Ant Design 组件

### 命名规范

- 组件文件使用 PascalCase：`UserList.tsx`
- 工具函数使用 camelCase：`formatDate.ts`
- 常量使用 UPPER_SNAKE_CASE：`API_BASE_URL`
- CSS 类名使用 kebab-case：`user-list-container`

### 文件组织

- 每个功能模块独立目录
- 页面组件与样式文件同目录
- API 文件按功能模块划分
- 类型定义统一在 `types/index.ts`

### Git 提交规范

- `feat`: 新功能
- `fix`: 修复问题
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具相关

## API 接口

### 认证相关

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/changePassword` - 修改密码

### 系统信息

- `GET /api/common/info` - 获取系统信息（平台名称、版本、版权等）

### 用户管理

- `GET /api/admin/user/list` - 用户列表
- `GET /api/admin/user/detail/{id}` - 用户详情
- `POST /api/admin/user/create` - 创建用户
- `PATCH /api/admin/user/edit` - 更新用户
- `DELETE /api/admin/user/delete` - 删除用户

更多 API 接口请参考 Swagger 文档或后端 API 文档。

## 浏览器支持

- Chrome (最新版本)
- Firefox (最新版本)
- Safari (最新版本)
- Edge (最新版本)

## 许可证

Copyright © 2025 Admin Pro 管理系统. All rights reserved.
