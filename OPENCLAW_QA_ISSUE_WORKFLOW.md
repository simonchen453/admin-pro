# OpenClaw QA → GitHub Issue → Codex 修复流程

本文档说明 `admin-pro` 当前使用的 OpenClaw 自动化协作流程，目标是把「页面测试 → 缺陷整理 → GitHub Issue → 开发修复 → 回归验证」串成一条可重复、可审计、可控的链路。

## 1. 角色分工

### `main`
- 负责总控、任务拆解、结果汇总
- 不直接做高风险开发或部署动作

### `qa-web`
- 负责页面 smoke test / 回归验证
- 发现可复现问题后，输出结构化 issue 草稿
- 只有得到明确批准时，才会创建正式 GitHub issue

### `codex`
- 负责处理带 `bug + auto-fix + agent:codex` 标签的 issue
- 进行本地代码修改和验证
- 默认不自动 commit / push / 开 PR

### `claude-code`
- 负责复杂问题、重构、测试补全、评审类任务
- 主要处理 `agent:claude`、`refactor`、`test-hardening` 类 issue

## 2. GitHub 约定

### 默认仓库
- `simonchen453/admin-pro`

### 默认 issue 语言
- **中文**
- 标题、正文、复现步骤、预期结果、实际结果默认全部使用中文
- 只有在用户明确要求时，才改成英文

### QA 缺陷默认标签
- `bug`
- `qa-found`
- `auto-fix`
- `agent:codex`

### 复杂问题标签
- `agent:claude`
- `refactor`
- `test-hardening`

## 3. QA 报 bug 的流转流程

### 第一步：执行页面测试
`qa-web` 按 smoke test 流程访问目标环境，默认检查：
1. 首页可加载
2. 登录成功
3. 核心业务路径成功
4. 退出登录成功

### 第二步：发现问题后先生成 issue 草稿
如果发现可复现问题，`qa-web` 不会直接发 GitHub，而是先输出结构化草稿，包含：
- 问题概述
- 环境信息
- 复现步骤
- 预期结果
- 实际结果
- 证据（截图 / 控制台 / API）
- 建议标签
- 建议处理人

### 第三步：人工确认后创建 GitHub issue
只有在用户明确给出 `APPROVE_ISSUE` 后，`qa-web` 才允许调用 `gh issue create` 创建正式 issue。

### 第四步：`codex` 自动接单
`codex` 轮询 GitHub 上满足以下条件的 open issues：
- 带 `bug`
- 带 `auto-fix`
- 带 `agent:codex`

命中后，`codex` 会：
1. 阅读 issue 内容
2. 提出最小修复计划
3. 修改本地代码
4. 跑最相关的验证
5. 返回修改文件、验证结果、剩余风险

### 第五步：提交和推送需要再次批准
即使修复完成，也不会直接提交到远端。以下动作都需要人工批准：
- `APPROVE_COMMIT`：允许 `git commit`
- `APPROVE_PUSH`：允许 `git push`
- `APPROVE_PR`：允许创建 PR

## 4. 本地开发环境的 QA 特殊规则

当前本地开发环境默认参数：
- 前端：`http://localhost:3000/adminpro/`
- 后端：`http://localhost:8080/adminpro`
- 默认测试账号：`superadmin / password$1`

### 登录验证码处理策略
本地开发环境里，登录页可能启用验证码。`qa-web` 处理顺序如下：
1. 优先复用已有登录态
2. 使用移动端 `User-Agent` 调用本地登录接口建立登录态
3. 如果仍被验证码阻塞，则请求 `/auth/captcha.jpg`，并从本地日志 `adminpro-web/logs/adminpro.log` 中读取最新验证码进行登录
4. 若仍无法完成，则返回 `BLOCKED: captcha`

说明：上述验证码日志兜底方案**仅用于本地开发环境**，不适用于生产或共享环境。

## 5. 当前仓库中的示例问题

已验证的一个实际问题：
- 登录接口在用户未配置头像时，返回了错误的 `avatarUrl`
- 错误示例：`http://127.0.0.1:8080null`

该问题已经作为 GitHub issue 创建：
- Issue: `#1 登录响应返回了无效的头像地址`

## 6. 推荐操作方式

### QA 产出 issue 草稿
让 `qa-web` 先只出草稿，不直接发 issue。

### 创建正式 issue
确认草稿内容无误后，再给出 `APPROVE_ISSUE`。

### 让 `codex` 处理问题
当 issue 已有正确标签后，允许 `codex` 轮询接单，或者直接指定它处理某个 issue。

### 修复完成后
- 先看本地验证结果
- 再决定是否给 `APPROVE_COMMIT`
- 如需上 GitHub，再给 `APPROVE_PUSH` / `APPROVE_PR`

## 7. 为什么这样设计

这套流程的目标不是“完全无人值守”，而是：
- 把重复劳动自动化
- 把高风险动作留给人工确认
- 保持问题可追踪、修复可审查、流程可复盘

也就是说：
- QA 可以自动发现和整理问题
- GitHub 可以承接问题流转
- 开发 agent 可以自动接单和修复
- 但最终提交和外发，仍然由人把关
