# JWT Token 多端认证系统技术规范

> **文档版本**: v1.0  
> **创建日期**: 2026-01-18  
> **状态**: 待实施  
> **审批人**: -

---

## 1. 概述

### 1.1 文档目的

本文档为 AdminPro 系统 **JWT Token 多端统一认证** 的完整技术规范，供开发人员按照此文档进行实现。

### 1.2 背景与问题

当前系统采用 **Session + Token 混合认证**：
- Web 端：HttpSession + Cookie (JSESSIONID)
- 移动端：数据库 Token 表 (user_token)

**存在问题**：
1. 认证逻辑分散，Web/Mobile 代码路径不同，维护成本高
2. Session 依赖服务器内存，水平扩展需要 Session 共享
3. 验证码存储在 Session，服务器重启后失效
4. 跨域场景 Cookie 处理复杂

### 1.3 目标

| 目标 | 描述 |
|------|------|
| 统一认证 | 所有端使用 JWT Token，一套代码处理 |
| 无状态 | 服务端不存储会话状态 (Sessionless) |
| 多端支持 | Web、iOS、Android、小程序 |
| 安全可靠 | 双 Token 机制，支持 Token 撤销 |

### 1.4 术语定义

| 术语 | 英文 | 说明 |
|------|------|------|
| Access Token | AT | 短期访问令牌，用于 API 认证 |
| Refresh Token | RT | 长期刷新令牌，用于获取新 AT |
| JWT | JSON Web Token | 自包含的 Token 格式 |
| JTI | JWT ID | Token 唯一标识，用于黑名单 |

---

## 2. 架构设计

### 2.1 目标架构

```
┌─────────────┐                         ┌─────────────────────┐
│   Web 端    │                         │                     │
├─────────────┤    Authorization:       │    后端服务          │
│   移动端    │    Bearer <JWT>         │    (Sessionless)    │
├─────────────┤ ───────────────────────▶│                     │
│   小程序    │                         │  JwtAuthFilter      │
└─────────────┘                         └──────────┬──────────┘
                                                   │
                                        ┌──────────┴──────────┐
                                        │                     │
                                   ┌────▼────┐          ┌─────▼─────┐
                                   │ Ehcache │          │  MySQL    │
                                   │ (Token) │          │ (用户设备) │
                                   └─────────┘          └───────────┘
```

### 2.2 主要变更

| 组件 | 当前实现 | 目标实现 |
|------|---------|---------|
| Web 认证 | HttpSession | JWT |
| Token 存储 | MySQL user_token 表 | Ehcache 内存缓存 |
| 验证码存储 | HttpSession | Ehcache |
| 认证过滤器 | AuthenticationFilter | JwtAuthenticationFilter |

---

## 3. Token 设计

### 3.1 Access Token (AT)

**格式**: JWT (JSON Web Token)  
**算法**: HS256 (HMAC-SHA256)

**Payload 结构**:

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "iss": "adminpro",
  "aud": "web",
  "iat": 1737187200,
  "exp": 1737188100,
  "jti": "a1b2c3d4-5678-90ab-cdef-1234567890ab",
  
  "userDomain": "system",
  "loginName": "admin",
  "realName": "管理员",
  "deptNo": "D001",
  "roles": ["admin"],
  "permissions": ["user:view", "user:edit"]
}
```

**字段说明**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sub | String | 是 | 用户ID（全局唯一主键） |
| iss | String | 是 | 签发者，固定值 "adminpro" |
| aud | String | 是 | 受众/平台: web/mobile/miniprogram |
| iat | Long | 是 | 签发时间（Unix 秒） |
| exp | Long | 是 | 过期时间（Unix 秒） |
| jti | String | 是 | Token ID，用于黑名单 |
| userDomain | String | 是 | 用户域 |
| loginName | String | 是 | 登录名 |
| realName | String | 否 | 真实姓名 |
| deptNo | String | 否 | 部门编号 |
| roles | String[] | 是 | 角色代码列表 |
| permissions | String[] | 否 | 权限代码列表 |

**有效期配置**：

| 平台 | 默认 | 配置项 |
|------|------|--------|
| Web | 15 分钟 | `app.jwt.access-token-validity.web` |
| Mobile | 30 分钟 | `app.jwt.access-token-validity.mobile` |
| MiniProgram | 30 分钟 | `app.jwt.access-token-validity.miniprogram` |

### 3.2 Refresh Token (RT)

**格式**: UUID + 前缀  
**示例**: `rt_550e8400e29b41d4a716446655440000`

**存储位置**: Ehcache `jwt:refresh_token` 缓存

**缓存 Value 结构**:

```java
public class RefreshTokenData {
    private String userId;          // 用户ID
    private String userDomain;      // 用户域
    private String loginName;       // 登录名
    private String platform;        // 平台
    private String deviceId;        // 设备标识
    private String deviceName;      // 设备名称
    private String ip;              // 登录IP
    private String userAgent;       // User-Agent
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime lastUsedAt;   // 最后使用时间
    private boolean rememberMe;     // 是否记住登录
}
```

**有效期配置**：

| 平台 | 默认 | 记住我 | 配置项 |
|------|------|--------|--------|
| Web | 7 天 | 30 天 | `app.jwt.refresh-token-validity.web` |
| Mobile | 30 天 | 90 天 | `app.jwt.refresh-token-validity.mobile` |
| MiniProgram | 30 天 | 90 天 | `app.jwt.refresh-token-validity.miniprogram` |

### 3.3 Token 签名密钥

**生产环境**：从环境变量读取

```bash
# 生成 256 位密钥
openssl rand -base64 32

# 设置环境变量
export JWT_SECRET=your_256_bit_secret_key_here
```

**开发环境**：使用默认值（仅用于开发）

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:dev-only-secret-key-do-not-use-in-production}
```

---

## 4. Ehcache 配置

### 4.1 新增缓存区域

在 `ehcache.xml` 中添加以下缓存配置：

```xml
<!-- JWT Refresh Token 缓存 -->
<cache alias="jwt:refresh_token">
  <key-type>java.lang.String</key-type>
  <value-type>com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData</value-type>
  <expiry>
    <ttl unit="days">30</ttl>
  </expiry>
  <heap unit="entries">10000</heap>
  <disk unit="MB" persistent="true">100</disk>
</cache>

<!-- JWT Token 黑名单 -->
<cache alias="jwt:token_blacklist">
  <key-type>java.lang.String</key-type>
  <value-type>java.lang.Long</value-type>
  <expiry>
    <ttl unit="hours">24</ttl>
  </expiry>
  <heap unit="entries">50000</heap>
</cache>

<!-- 用户 Token 索引（用于批量撤销） -->
<cache alias="jwt:user_tokens">
  <key-type>java.lang.String</key-type>
  <value-type>java.util.Set</value-type>
  <expiry>
    <ttl unit="days">30</ttl>
  </expiry>
  <heap unit="entries">10000</heap>
</cache>
```

### 4.2 缓存常量

```java
public class JwtCacheConstants {
    /** Refresh Token 缓存名 */
    public static final String REFRESH_TOKEN_CACHE = "jwt:refresh_token";
    
    /** Token 黑名单缓存名 */
    public static final String TOKEN_BLACKLIST_CACHE = "jwt:token_blacklist";
    
    /** 用户 Token 索引缓存名 */
    public static final String USER_TOKENS_CACHE = "jwt:user_tokens";
}
```

---

## 5. API 接口规范

### 5.1 登录接口

```
POST /api/v1/auth/login
Content-Type: application/json
```

**请求体**:

```json
{
  "loginName": "admin",
  "password": "123456",
  "domain": "system",
  "captcha": "5",
  "platform": "web",
  "deviceId": "browser_fp_abc123",
  "deviceName": "Chrome on Windows",
  "rememberMe": false
}
```

> **注意**: 当前代码使用 `userId` 作为登录名字段。建议保持现有 API 兼容性，内部统一使用 `loginName`。

**请求字段说明**:

| 字段 | 类型 | Web | Mobile | 说明 |
|------|------|-----|--------|------|
| loginName | String | 必填 | 必填 | 登录名（VO 中为 userId） |
| password | String | 必填 | 必填 | 密码 |
| domain | String | 必填 | 必填 | 用户域 |
| captcha | String | 必填 | - | 验证码答案 |
| platform | String | 必填 | 必填 | 平台: web/mobile/miniprogram |
| deviceId | String | 推荐 | 必填 | 设备唯一标识 |
| deviceName | String | 可选 | 可选 | 设备名称 |
| rememberMe | Boolean | 可选 | 可选 | 记住登录（延长 RT 有效期） |

**成功响应** (HTTP 200, restCode=200):

```json
{
  "restCode": "200",
  "message": "登录成功",
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "rt_550e8400e29b41d4a716446655440000",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "loginName": "admin",
      "realName": "管理员",
      "avatarUrl": "/avatars/admin.png",
      "roles": ["admin"]
    }
  }
}
```

**失败响应**:

| restCode | 原因 | 处理建议 |
|----------|------|---------|
| 4001 | 用户名或密码错误 | 提示用户 |
| 4002 | 验证码错误/过期 | 刷新验证码 |
| 4003 | 账户已锁定 | 联系管理员 |
| 4004 | 账户已停用 | 联系管理员 |
| 4005 | 设备数超限 | 提示先登出其他设备 |

### 5.2 刷新 Token 接口

```
POST /api/v1/auth/refresh
Content-Type: application/json
```

**请求体**:

```json
{
  "refreshToken": "rt_550e8400e29b41d4a716446655440000"
}
```

**成功响应**:

```json
{
  "restCode": "200",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "rt_660e9500f30c52e5b827557766551111",
    "expiresIn": 900
  }
}
```

> **重要**: 启用 Token 轮换时，每次刷新返回新的 Refresh Token，旧的立即失效。

**失败响应**:

| restCode | 原因 | 处理建议 |
|----------|------|---------|
| 4011 | Refresh Token 无效 | 重新登录 |
| 4012 | Refresh Token 过期 | 重新登录 |
| 4013 | Token 已被撤销 | 重新登录 |

### 5.3 登出接口

```
POST /api/v1/auth/logout
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**请求体** (可选):

```json
{
  "refreshToken": "rt_550e8400e29b41d4a716446655440000",
  "logoutAll": false
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| refreshToken | String | 要撤销的 RT（可选） |
| logoutAll | Boolean | 是否登出所有设备 |

**响应**:

```json
{
  "restCode": "200",
  "message": "登出成功",
  "data": {
    "revokedCount": 1
  }
}
```

### 5.4 获取设备列表

```
GET /api/v1/auth/devices
Authorization: Bearer <accessToken>
```

**响应**:

```json
{
  "restCode": "200",
  "data": [
    {
      "id": "device_001",
      "platform": "web",
      "deviceName": "Chrome on Windows",
      "ip": "192.168.1.100",
      "lastActiveAt": "2026-01-18T12:00:00Z",
      "isCurrent": true
    },
    {
      "id": "device_002",
      "platform": "mobile",
      "deviceName": "iPhone 15 Pro",
      "ip": "192.168.1.101",
      "lastActiveAt": "2026-01-17T18:30:00Z",
      "isCurrent": false
    }
  ]
}
```

### 5.5 踢出指定设备

```
DELETE /api/v1/auth/devices/{deviceId}
Authorization: Bearer <accessToken>
```

---

## 6. 安全设计

### 6.1 认证策略：白名单机制 (Reference Token)

系统采用 **强一致性白名单** 策略，而非标准的无状态 JWT。

**验证流程**:
1. 校验 Token 签名和结构。
2. **关键步骤**: 检查 Token (通过 `jti`) 是否存在于 `jwt:access_token` 缓存中。
3. 如果缓存中不存在（已过期、已注销、服务重启），则视为无效。

**优点**:
- **即时失效**: 登出或强制下线立即生效，无时间窗口风险。
- **重启安全**: 服务重启导致缓存清空时，所有旧 Token 自动失效，用户需重新登录 (Fail-Safe)。
- **简单可控**: 无需维护复杂的黑名单逻辑。

### 6.2 客户端 Token 存储策略

| 平台 | Access Token | Refresh Token |
|------|--------------|---------------|
| **Web** | JS 内存 (Zustand Store) | HttpOnly Secure Cookie |
| **iOS** | Keychain | Keychain |
| **Android** | EncryptedSharedPreferences | EncryptedSharedPreferences |
| **小程序** | 内存 | wx.setStorageSync (加密) |

### 6.3 Token 轮换与并发

- **Access Token**: 生成后存入 Ehcache，设置与 JWT `exp` 一致的 TTL。
- **Refresh Token**: 轮换时，删除旧 Access Token 和旧 Refresh Token，生成新的一对。

### 6.4 缓存结构

```java
// Access Token 缓存 (白名单)
// Key: "at_" + jti
// Value: userId (或简单 boolean)
// TTL: 15分钟 (与 Token exp 一致)
```

```java
// Refresh Token 缓存
// Key: "rt_" + token
// Value: RefreshTokenData
// TTL: 7-30天
```

### 6.5 速率限制

| 接口 | 限制 | 窗口 | 超限响应 |
|------|------|------|---------|
| POST /auth/login | 5 次/IP | 1 分钟 | 429 Too Many Requests |
| POST /auth/refresh | 30 次/用户 | 1 分钟 | 429 |
| POST /auth/logout | 10 次/用户 | 1 分钟 | 429 |

---

## 7. 数据模型

### 7.1 用户设备表 (新增)

```sql
CREATE TABLE `user_device` (
  `id` VARCHAR(36) NOT NULL COMMENT '设备记录ID',
  `user_id` VARCHAR(36) NOT NULL COMMENT '用户ID',
  `device_id` VARCHAR(255) DEFAULT NULL COMMENT '客户端设备标识',
  `platform` VARCHAR(20) NOT NULL COMMENT '平台: web/mobile/miniprogram',
  `device_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `refresh_token_jti` VARCHAR(36) DEFAULT NULL COMMENT '当前有效的 RT JTI',
  `last_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后活跃IP',
  `last_user_agent` TEXT COMMENT '最后 User-Agent',
  `last_active_at` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否活跃',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_refresh_token_jti` (`refresh_token_jti`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';
```

### 7.2 Liquibase 变更集

```xml
<changeSet id="20260118-add-user-device-table" author="adminpro">
    <createTable tableName="user_device" remarks="用户设备表">
        <column name="id" type="VARCHAR(36)">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="user_id" type="VARCHAR(36)">
            <constraints nullable="false"/>
        </column>
        <column name="device_id" type="VARCHAR(255)"/>
        <column name="platform" type="VARCHAR(20)">
            <constraints nullable="false"/>
        </column>
        <column name="device_name" type="VARCHAR(100)"/>
        <column name="refresh_token_jti" type="VARCHAR(36)"/>
        <column name="last_ip" type="VARCHAR(50)"/>
        <column name="last_user_agent" type="TEXT"/>
        <column name="last_active_at" type="DATETIME"/>
        <column name="is_active" type="TINYINT(1)" defaultValue="1"/>
        <column name="created_at" type="DATETIME" defaultValueComputed="CURRENT_TIMESTAMP"/>
        <column name="updated_at" type="DATETIME" defaultValueComputed="CURRENT_TIMESTAMP"/>
    </createTable>
    
    <createIndex indexName="idx_user_device_user_id" tableName="user_device">
        <column name="user_id"/>
    </createIndex>
</changeSet>
```

---

*（文档续见 Part 2: 后端实现详解）*
