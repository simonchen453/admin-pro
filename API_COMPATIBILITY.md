# API 多端兼容性说明

本文档说明后端API如何同时支持React Web前端、Android App和小程序调用。

## 支持的客户端类型

系统支持以下客户端类型：

- **WEB**: Web浏览器（React前端）
- **ANDROID**: Android原生应用
- **IOS**: iOS原生应用
- **WECHAT_MINI_PROGRAM**: 微信小程序
- **ALIPAY_MINI_PROGRAM**: 支付宝小程序

## 客户端识别机制

### 1. 通过请求头识别（推荐）

客户端可以在请求头中明确指定客户端类型：

```
X-Client-Type: ANDROID
X-Client-Type: IOS
X-Client-Type: WECHAT_MINI_PROGRAM
X-Client-Type: ALIPAY_MINI_PROGRAM
```

### 2. 自动检测

如果未指定 `X-Client-Type` 头，系统会根据以下规则自动检测：

- **微信小程序**: User-Agent包含`MicroMessenger`且Referer包含`servicewechat.com`或`servicewechat.net`，或存在`X-WX-Source: miniprogram`头
- **支付宝小程序**: User-Agent包含`AlipayClient`且Referer包含`alipay.com`或`alipaydev.com`，或存在`X-Alipay-Source: miniprogram`头
- **Android App**: User-Agent包含`android`且不包含`wv`、`micromessenger`、`alipayclient`
- **iOS App**: User-Agent匹配`iphone|ipad|ipod`且不包含`micromessenger`、`alipayclient`
- **Web**: 其他情况默认识别为Web

## 认证机制

### Token认证方式

系统支持以下三种Token传递方式（按优先级排序）：

1. **标准Bearer Token**（推荐移动端使用）
   ```
   Authorization: Bearer <token>
   ```

2. **自定义Header**（兼容现有前端）
   ```
   x-access-token: <token>
   ```

3. **Query参数**（兼容旧版本）
   ```
   ?x-access-token=<token>
   ```

### 登录接口

**接口地址**: `POST /rest/auth/login`

**请求体**:
```json
{
  "userId": "用户名",
  "password": "密码",
  "platform": "SYSTEM|INTERNET|INTRANET",
  "captcha": "验证码（Web端必填，移动端和小程序可省略）"
}
```

**响应**:
```json
{
  "restCode": "200",
  "success": true,
  "data": {
    "token": "认证令牌",
    "userId": "用户名",
    "id": "用户ID",
    ...
  },
  "timestamp": 1234567890,
  "requestId": "请求ID"
}
```

## 请求头规范

### 推荐请求头

移动端和小程序建议在请求中包含以下请求头：

```
X-Client-Type: ANDROID|IOS|WECHAT_MINI_PROGRAM|ALIPAY_MINI_PROGRAM
X-App-Version: 1.0.0
X-Device-Id: 设备唯一标识
X-Request-Id: 请求ID（可选，系统会自动生成）
Authorization: Bearer <token>
```

### 响应头

系统会在响应中包含以下头信息：

```
X-Request-Id: 请求ID（用于日志追踪）
```

## CORS配置

### 小程序域名

系统已自动配置以下小程序域名支持：

- `https://servicewechat.com`
- `https://servicewechat.net`
- `https://*.servicewechat.com`
- `https://*.servicewechat.net`
- `https://alipay.com`
- `https://alipaydev.com`
- `https://*.alipay.com`
- `https://*.alipaydev.com`

### 自定义配置

在 `application.yml` 中配置：

```yaml
app:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:5173
    allow-all-origins: false  # 生产环境建议设为false
```

## 响应格式

所有API响应统一使用以下格式：

```json
{
  "restCode": "200",
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1234567890,
  "requestId": "请求ID",
  "errors": [],
  "errorsMap": {}
}
```

### 字段说明

- `restCode`: HTTP状态码或业务错误码
- `success`: 请求是否成功
- `message`: 响应消息
- `data`: 响应数据
- `timestamp`: 响应时间戳（毫秒）
- `requestId`: 请求唯一标识（用于日志追踪）
- `errors`: 错误列表
- `errorsMap`: 字段级错误映射

## 客户端类型判断

在Controller中可以通过以下方式获取客户端信息：

```java
import com.adminpro.framework.common.helper.ClientHelper;
import com.adminpro.framework.common.enums.ClientType;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MyController {
    
    @RequestMapping("/api/test")
    public R test(HttpServletRequest request) {
        // 获取客户端类型
        ClientType clientType = ClientHelper.detectClientType(request);
        
        // 判断是否为移动App
        boolean isMobileApp = ClientHelper.isMobileAppRequest(request);
        
        // 判断是否为小程序
        boolean isMiniProgram = ClientHelper.isMiniProgramRequest(request);
        
        // 判断是否为移动端（App + 小程序）
        boolean isMobile = ClientHelper.isMobileRequest(request);
        
        // 获取App版本
        String appVersion = ClientHelper.getAppVersion(request);
        
        // 获取设备ID
        String deviceId = ClientHelper.getDeviceId(request);
        
        // 从请求属性中获取（由拦截器设置）
        ClientType type = (ClientType) request.getAttribute("clientType");
        String requestId = (String) request.getAttribute("requestId");
        
        return R.ok();
    }
}
```

## 验证码策略

- **Web端**: 需要验证码（开发模式除外）
- **移动App**: 不需要验证码
- **小程序**: 不需要验证码

## 日志追踪

系统会自动记录以下信息到日志上下文（MDC）：

- `requestId`: 请求ID
- `clientType`: 客户端类型
- `appVersion`: App版本（如果提供）
- `deviceId`: 设备ID（如果提供）

这些信息会在所有日志输出中自动包含，方便问题排查。

## 最佳实践

### Android/iOS App

1. 在请求头中明确指定 `X-Client-Type`
2. 使用 `Authorization: Bearer <token>` 方式传递Token
3. 在登录后保存Token，后续请求自动携带
4. 实现Token刷新机制

### 小程序

1. 微信小程序会自动识别，无需额外配置
2. 支付宝小程序会自动识别，无需额外配置
3. 使用 `Authorization: Bearer <token>` 方式传递Token
4. 注意小程序的网络请求域名白名单配置

### Web前端

1. 使用现有的 `x-access-token` header方式（已兼容）
2. 或升级为 `Authorization: Bearer <token>` 方式
3. 注意CORS配置

## 常见问题

### Q: 小程序请求被CORS拦截？

A: 确保小程序的后台配置中已添加服务器域名到白名单。系统已自动支持小程序域名。

### Q: 如何区分不同客户端做不同处理？

A: 使用 `ClientHelper.detectClientType(request)` 获取客户端类型，然后根据类型做相应处理。

### Q: Token过期如何处理？

A: 客户端应检测到401响应后，引导用户重新登录。

### Q: 如何追踪某个请求的完整日志？

A: 使用响应头中的 `X-Request-Id`，在日志中搜索该ID即可找到该请求的所有相关日志。

