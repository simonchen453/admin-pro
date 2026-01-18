# Swagger API 文档注解批量修改指南

## 背景说明

本项目采用**统一响应格式**，所有 API 接口的 HTTP 状态码始终返回 `200`，通过响应体中的 `restCode` 字段区分业务状态。

### 响应格式示例

```json
{
  "restCode": "200",        // 业务状态码：200=成功, 401=未授权, 403=无权限, 500=错误
  "message": "操作成功",
  "success": true,
  "errors": [],
  "errorsMap": {},
  "data": { ... },          // 具体数据，类型根据接口不同而变化
  "timestamp": 1705546800000,
  "requestId": "abc-123-def"
}
```

### 问题

之前的 Swagger 注解使用了多个 `@ApiResponse` 配置相同的 `responseCode = "200"`，这在 OpenAPI 规范中是不合法的，会导致只显示最后一个定义。

---

## 修改规范

### 1. 统一 `@ApiResponse` 注解模板

将所有 `@ApiResponses` 替换为单个 `@ApiResponse`，格式如下：

```java
@ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: [成功描述]，data 字段包含 [数据类型描述]
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
)
```

### 2. 根据接口类型调整描述

| 操作类型 | restCode=200 描述示例 |
|---------|---------------------|
| 查询列表 | `查询成功，data 字段包含 List<XxxEntity> 列表` |
| 查询详情 | `查询成功，data 字段包含 XxxEntity 对象` |
| 创建 | `创建成功，data 字段包含新创建的 XxxEntity` |
| 更新 | `更新成功` |
| 删除 | `删除成功` |
| 批量删除 | `批量删除成功` |

### 3. 可选的额外状态码

根据接口特性，可以添加额外的业务状态码说明：

```java
// 查询详情接口可能需要 404
- restCode=404: 资源不存在

// 创建/更新接口可能需要 400
- restCode=400: 请求参数错误
```

---

## 修改前后对比

### ❌ 修改前（错误示例）

```java
@Operation(summary = "查询菜单权限列表", description = "...")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(...)),
        @ApiResponse(responseCode = "200", description = "未授权（restCode=401）"),
        @ApiResponse(responseCode = "200", description = "无权限访问（restCode=403）")
})
@PostMapping(value = "/search")
public R<List<MenuEntity>> list(@RequestBody SearchForm searchForm) {
```

### ✅ 修改后（正确示例）

```java
@Operation(summary = "查询菜单权限列表", description = "...")
@ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 List<MenuEntity> 菜单列表
                - restCode=401: 未授权，需要登录
                - restCode=403: 无权限访问
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
)
@PostMapping(value = "/search")
public R<List<MenuEntity>> list(@RequestBody SearchForm searchForm) {
```

---

## 待修改的 Controller 文件列表

以下文件需要按照上述规范进行修改：

### RBAC 模块 (`com.adminpro.system.web.rbac`)

| 文件 | 路径 |
|-----|------|
| `MenuController.java` | ✅ 已完成（作为示例） |
| `UserController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/rbac/UserController.java` |
| `RoleController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/rbac/RoleController.java` |
| `DeptController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/rbac/DeptController.java` |
| `PostController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/rbac/PostController.java` |
| `DomainController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/rbac/DomainController.java` |
| `AuthController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/rbac/AuthController.java` |

### Tools 模块 (`com.adminpro.system.web.tools`)

| 文件 | 路径 |
|-----|------|
| `DictController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/DictController.java` |
| `DictDataController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/DictDataController.java` |
| `SysLogController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/SysLogController.java` |
| `AuditLogController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/AuditLogController.java` |
| `ExceptionLogController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/ExceptionLogController.java` |
| `SessionController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/SessionController.java` |
| `ScheduleJobController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/ScheduleJobController.java` |
| `ConfigController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/tools/ConfigController.java` |

### 公共模块

| 文件 | 路径 |
|-----|------|
| `CommonController.java` | `adminpro-system/src/main/java/com/adminpro/system/web/CommonController.java` |

---

## 修改步骤（每个文件）

### Step 1: 查找需要修改的注解

搜索以下模式：
```
@ApiResponses(value = {
```

### Step 2: 分析接口返回类型

查看方法的返回类型，例如：
- `R<List<MenuEntity>>` → data 字段包含 `List<MenuEntity>`
- `R<UserEntity>` → data 字段包含 `UserEntity`
- `R<Void>` 或 `R<?>` → 无 data 字段或不关注

### Step 3: 替换注解

使用模板替换，确保：
1. 移除 `@ApiResponses` 包装
2. 使用单个 `@ApiResponse`
3. 在 description 中列出所有业务状态码
4. `schema = @Schema(implementation = R.class)`

### Step 4: 清理未使用的导入

修改后可能需要移除：
```java
import io.swagger.v3.oas.annotations.responses.ApiResponses;
```

### Step 5: 编译验证

```bash
mvn compile -DskipTests -q
```

---

## 已完成的基础配置

以下配置已经完成，**无需重复修改**：

### 1. R 类已添加 Swagger 注解

文件：`adminpro-framework/src/main/java/com/adminpro/framework/base/entity/R.java`

```java
@Schema(description = "统一响应格式")
public class R<T> implements Serializable {
    @Schema(description = "业务状态码：200=成功, 400=参数错误, 401=未授权, 403=无权限, 404=不存在, 500=服务器错误", example = "200")
    private String restCode;
    // ... 其他字段都已添加 @Schema
}
```

### 2. SwaggerConfig 已配置移除默认响应

文件：`adminpro-system/src/main/java/com/adminpro/system/config/SwaggerConfig.java`

```java
@Bean
public OperationCustomizer removeDefaultResponses() {
    return (operation, handlerMethod) -> {
        if (operation.getResponses() != null) {
            operation.getResponses().remove("400");
            operation.getResponses().remove("401");
            operation.getResponses().remove("403");
            operation.getResponses().remove("404");
            operation.getResponses().remove("500");
        }
        return operation;
    };
}
```

---

## 验证方法

修改完成后：

1. 编译项目：`mvn compile -DskipTests`
2. 启动应用
3. 访问 Swagger UI：`http://localhost:8080/adminpro/swagger-ui/index.html`
4. 检查每个接口只显示 200 响应，且 description 包含完整的业务状态码说明

---

## 注意事项

1. **保持 `@Operation` 不变** - 只修改 `@ApiResponse` / `@ApiResponses`
2. **保持 `@Parameter` 不变** - 参数注解无需修改
3. **使用 Java 17+ Text Block** - 多行字符串使用 `"""` 语法
4. **统一使用 `R.class`** - 不需要为每个接口创建单独的 Response 类
5. **编译验证** - 每修改一个文件后建议立即编译验证
