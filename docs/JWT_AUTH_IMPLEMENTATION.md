# JWT Token 多端认证 - 后端实现详解

> **关联文档**: [JWT_AUTH_DESIGN.md](./JWT_AUTH_DESIGN.md)  
> **本文档**: 后端实现细节，供开发人员参照实现

---

## 8. 后端实现

### 8.1 新增类清单

| 包路径 | 类名 | 说明 |
|--------|------|------|
| `.config` | `JwtProperties` | JWT 配置属性 |
| `.core.security.jwt` | `JwtTokenProvider` | JWT 生成/解析/验证 |
| `.core.security.jwt` | `JwtAuthenticationFilter` | 认证过滤器 |
| `.core.security.jwt` | `JwtAuthenticationToken` | 认证令牌对象 |
| `.rbac.api` | `RefreshTokenService` | RT 管理 |
| `.rbac.api` | `TokenBlacklistService` | 黑名单管理 |
| `.rbac.domains.entity.device` | `UserDeviceEntity` | 设备实体 |
| `.rbac.domains.entity.device` | `UserDeviceDao` | 设备 DAO |
| `.rbac.domains.entity.device` | `UserDeviceService` | 设备服务 |
| `.rbac.domains.entity.jwt` | `RefreshTokenData` | RT 缓存数据 |
| `.rbac.domains.vo.login` | `JwtLoginResponse` | 登录响应 VO |

### 8.2 配置类

**文件**: `JwtProperties.java`

```java
package com.adminpro.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    
    /**
     * JWT 签名密钥（生产环境必须从环境变量读取）
     */
    private String secret = "dev-only-secret-key-do-not-use-in-production";
    
    /**
     * 签名算法
     */
    private String algorithm = "HS256";
    
    /**
     * 签发者
     */
    private String issuer = "adminpro";
    
    /**
     * Access Token 有效期（秒），按平台配置
     */
    private Map<String, Integer> accessTokenValidity = Map.of(
        "web", 900,          // 15分钟
        "mobile", 1800,      // 30分钟
        "miniprogram", 1800  // 30分钟
    );
    
    /**
     * Refresh Token 有效期（秒），按平台配置
     */
    private Map<String, Integer> refreshTokenValidity = Map.of(
        "web", 604800,        // 7天
        "mobile", 2592000,    // 30天
        "miniprogram", 2592000
    );
    
    /**
     * 记住我时 Refresh Token 有效期（秒）
     */
    private Map<String, Integer> rememberMeValidity = Map.of(
        "web", 2592000,       // 30天
        "mobile", 7776000,    // 90天
        "miniprogram", 7776000
    );
    
    /**
     * 是否启用 Refresh Token 轮换
     */
    private boolean enableRefreshRotation = true;
    
    /**
     * 每用户最大设备数
     */
    private int maxDevicesPerUser = 5;
    
    /**
     * 是否检测 Token 重放攻击
     */
    private boolean detectTokenReuse = true;
    
    public int getAccessTokenValidity(String platform) {
        return accessTokenValidity.getOrDefault(platform, 900);
    }
    
    public int getRefreshTokenValidity(String platform, boolean rememberMe) {
        if (rememberMe) {
            return rememberMeValidity.getOrDefault(platform, 2592000);
        }
        return refreshTokenValidity.getOrDefault(platform, 604800);
    }
}
```

### 8.3 JWT Token Provider

**文件**: `JwtTokenProvider.java`

```java
package com.adminpro.system.core.security.jwt;

import com.adminpro.system.config.JwtProperties;
import com.adminpro.system.core.security.auth.LoginUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final JwtProperties jwtProperties;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成 Access Token
     */
    public String generateAccessToken(LoginUser user, String platform) {
        Date now = new Date();
        int validity = jwtProperties.getAccessTokenValidity(platform);
        Date expiry = new Date(now.getTime() + validity * 1000L);
        String jti = UUID.randomUUID().toString();
        
        return Jwts.builder()
            .setSubject(user.getUserId())
            .setIssuer(jwtProperties.getIssuer())
            .setAudience(platform)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setId(jti)
            .claim("userDomain", user.getUserDomain())
            .claim("loginName", user.getLoginName())
            .claim("realName", user.getRealName())
            .claim("deptNo", user.getDeptNo())
            .claim("roles", user.getRoles())
            .claim("permissions", user.getPermissions())
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken() {
        return "rt_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 解析 Token（不验证过期）
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            // 返回过期的 claims，让调用方决定如何处理
            return e.getClaims();
        }
    }
    
    /**
     * 验证 Token 有效性
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.debug("JWT 验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 获取 Token 剩余有效期（秒）
     */
    public long getRemainingSeconds(String token) {
        Claims claims = parseToken(token);
        long expTime = claims.getExpiration().getTime();
        long remaining = (expTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
    
    /**
     * 从 Token 获取 JTI
     */
    public String getJti(String token) {
        return parseToken(token).getId();
    }
    
    /**
     * 从 Token 获取用户ID
     */
    public String getUserId(String token) {
        return parseToken(token).getSubject();
    }
    
    /**
     * 从 Token 获取平台
     */
    public String getPlatform(String token) {
        return parseToken(token).getAudience();
    }
}
```

### 8.4 Refresh Token Service

**文件**: `RefreshTokenService.java`

```java
package com.adminpro.system.rbac.api;

import com.adminpro.system.config.JwtProperties;
import com.adminpro.system.core.cache.AppCache;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.core.security.jwt.JwtCacheConstants;
import com.adminpro.system.core.security.jwt.JwtTokenProvider;
import com.adminpro.system.rbac.domains.entity.jwt.RefreshTokenData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final JwtProperties jwtProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService blacklistService;
    
    /**
     * 创建并存储 Refresh Token
     */
    public String createRefreshToken(LoginUser user, String platform, 
            String deviceId, String deviceName, String ip, 
            String userAgent, boolean rememberMe) {
        
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        int validity = jwtProperties.getRefreshTokenValidity(platform, rememberMe);
        
        RefreshTokenData data = new RefreshTokenData();
        data.setUserId(user.getUserId());
        data.setUserDomain(user.getUserDomain());
        data.setLoginName(user.getLoginName());
        data.setPlatform(platform);
        data.setDeviceId(deviceId);
        data.setDeviceName(deviceName);
        data.setIp(ip);
        data.setUserAgent(userAgent);
        data.setCreatedAt(LocalDateTime.now());
        data.setLastUsedAt(LocalDateTime.now());
        data.setRememberMe(rememberMe);
        
        // 存储到缓存
        AppCache.getInstance().set(
            JwtCacheConstants.REFRESH_TOKEN_CACHE,
            refreshToken,
            data,
            validity
        );
        
        // 添加到用户 Token 索引
        addToUserTokenIndex(user.getUserId(), refreshToken);
        
        log.debug("创建 RefreshToken: userId={}, platform={}, validity={}s",
            user.getUserId(), platform, validity);
        
        return refreshToken;
    }
    
    /**
     * 验证并获取 Refresh Token 数据
     */
    public RefreshTokenData validateAndGet(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("rt_")) {
            return null;
        }
        
        // 检查黑名单
        if (blacklistService.isBlacklisted(refreshToken)) {
            log.warn("Refresh Token 在黑名单中: {}", refreshToken);
            return null;
        }
        
        return AppCache.getInstance().get(
            JwtCacheConstants.REFRESH_TOKEN_CACHE,
            refreshToken,
            RefreshTokenData.class
        );
    }
    
    /**
     * 轮换 Refresh Token
     */
    public String rotateRefreshToken(String oldRefreshToken, LoginUser user,
            String platform, String deviceId, String deviceName,
            String ip, String userAgent) {
        
        RefreshTokenData oldData = validateAndGet(oldRefreshToken);
        if (oldData == null) {
            throw new IllegalArgumentException("无效的 Refresh Token");
        }
        
        // 删除旧 Token
        revokeRefreshToken(oldRefreshToken);
        
        // 创建新 Token
        return createRefreshToken(user, platform, deviceId, deviceName,
            ip, userAgent, oldData.isRememberMe());
    }
    
    /**
     * 撤销 Refresh Token
     */
    public void revokeRefreshToken(String refreshToken) {
        RefreshTokenData data = validateAndGet(refreshToken);
        if (data != null) {
            // 从缓存删除
            AppCache.getInstance().delete(
                JwtCacheConstants.REFRESH_TOKEN_CACHE,
                refreshToken
            );
            
            // 加入黑名单（短时间，应对网络重试）
            blacklistService.blacklistToken(refreshToken, 60);
            
            // 从用户索引移除
            removeFromUserTokenIndex(data.getUserId(), refreshToken);
            
            log.debug("撤销 RefreshToken: userId={}", data.getUserId());
        }
    }
    
    /**
     * 撤销用户所有 Token
     */
    public int revokeAllUserTokens(String userId) {
        Set<String> tokens = getUserTokens(userId);
        int count = 0;
        for (String token : tokens) {
            revokeRefreshToken(token);
            count++;
        }
        
        // 清空索引
        AppCache.getInstance().delete(JwtCacheConstants.USER_TOKENS_CACHE, userId);
        
        log.info("撤销用户所有 Token: userId={}, count={}", userId, count);
        return count;
    }
    
    /**
     * 获取用户所有 Token
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUserTokens(String userId) {
        Set<String> tokens = AppCache.getInstance().get(
            JwtCacheConstants.USER_TOKENS_CACHE,
            userId,
            Set.class
        );
        return tokens != null ? tokens : new HashSet<>();
    }
    
    private void addToUserTokenIndex(String userId, String refreshToken) {
        Set<String> tokens = getUserTokens(userId);
        tokens.add(refreshToken);
        AppCache.getInstance().set(
            JwtCacheConstants.USER_TOKENS_CACHE,
            userId,
            tokens
        );
    }
    
    private void removeFromUserTokenIndex(String userId, String refreshToken) {
        Set<String> tokens = getUserTokens(userId);
        tokens.remove(refreshToken);
        if (tokens.isEmpty()) {
            AppCache.getInstance().delete(JwtCacheConstants.USER_TOKENS_CACHE, userId);
        } else {
            AppCache.getInstance().set(
                JwtCacheConstants.USER_TOKENS_CACHE,
                userId,
                tokens
            );
        }
    }
}
```

### 8.5 JWT 认证过滤器

**文件**: `JwtAuthenticationFilter.java`

```java
package com.adminpro.system.core.security.jwt;

import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService blacklistService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String jti = jwtTokenProvider.getJti(token);
            
            // 白名单校验：检查 Token 是否在缓存中
            String cachedUserId = AppCache.getInstance().get(
                JwtCacheConstants.ACCESS_TOKEN_CACHE, 
                jti, 
                String.class
            );
            
            if (cachedUserId == null) {
                log.debug("Token 不在白名单中 (已失效或服务重启): jti={}", jti);
                filterChain.doFilter(request, response);
                return;
            }
            
            // 解析并设置认证信息
            Claims claims = jwtTokenProvider.parseToken(token);
            LoginUser loginUser = buildLoginUser(claims);
            
            List<SimpleGrantedAuthority> authorities = loginUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
            
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("JWT 认证成功: userId={}, loginName={}",
                loginUser.getUserId(), loginUser.getLoginName());
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private LoginUser buildLoginUser(Claims claims) {
        LoginUser user = new LoginUser();
        user.setUserId(claims.getSubject());
        user.setUserDomain(claims.get("userDomain", String.class));
        user.setLoginName(claims.get("loginName", String.class));
        user.setRealName(claims.get("realName", String.class));
        user.setDeptNo(claims.get("deptNo", String.class));
        user.setRoles((List<String>) claims.get("roles"));
        user.setPermissions((List<String>) claims.get("permissions"));
        return user;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 排除公开接口
        return path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/refresh") ||
               path.startsWith("/api/v1/auth/captcha") ||
               path.startsWith("/api/v1/common/release-info");
    }
}
```

### 8.6 配置文件示例

**application.yml**:

```yaml
app:
  jwt:
    # 签名密钥（生产环境必须从环境变量读取）
    secret: ${JWT_SECRET:dev-only-secret-key-do-not-use-in-production-32bytes}
    algorithm: HS256
    issuer: adminpro
    
    # Access Token 有效期（秒）
    access-token-validity:
      web: 900           # 15分钟
      mobile: 1800       # 30分钟
      miniprogram: 1800
    
    # Refresh Token 有效期（秒）
    refresh-token-validity:
      web: 604800        # 7天
      mobile: 2592000    # 30天
      miniprogram: 2592000
    
    # 记住我时 Refresh Token 有效期
    remember-me-validity:
      web: 2592000       # 30天
      mobile: 7776000    # 90天
      miniprogram: 7776000
    
    # 安全配置
    enable-refresh-rotation: true
    max-devices-per-user: 5
    detect-token-reuse: true

spring:
  cache:
    type: jcache
    jcache:
      config: classpath:ehcache.xml
      provider: org.ehcache.jsr107.EhcacheCachingProvider
```

---

## 9. 前端实现

### 9.1 Auth Store (Zustand)

```typescript
// stores/authStore.ts
import { create } from 'zustand';
import { authApi } from '../api/auth';

interface AuthState {
  accessToken: string | null;
  expiresAt: number | null;
  user: UserInfo | null;
  isRefreshing: boolean;
  refreshPromise: Promise<void> | null;
  
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<void>;
  clearAuth: () => void;
}

let refreshTimer: number | null = null;

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: null,
  expiresAt: null,
  user: null,
  isRefreshing: false,
  refreshPromise: null,
  
  login: async (credentials) => {
    const response = await authApi.login(credentials);
    const expiresAt = Date.now() + response.expiresIn * 1000;
    
    set({
      accessToken: response.accessToken,
      expiresAt,
      user: response.user,
    });
    
    scheduleRefresh(response.expiresIn);
  },
  
  logout: async () => {
    try {
      await authApi.logout();
    } finally {
      get().clearAuth();
    }
  },
  
  refreshToken: async () => {
    const state = get();
    
    // 防止并发刷新
    if (state.isRefreshing && state.refreshPromise) {
      return state.refreshPromise;
    }
    
    const promise = (async () => {
      set({ isRefreshing: true });
      try {
        const response = await authApi.refresh();
        const expiresAt = Date.now() + response.expiresIn * 1000;
        
        set({
          accessToken: response.accessToken,
          expiresAt,
          isRefreshing: false,
          refreshPromise: null,
        });
        
        scheduleRefresh(response.expiresIn);
      } catch (error) {
        set({ isRefreshing: false, refreshPromise: null });
        get().clearAuth();
        throw error;
      }
    })();
    
    set({ refreshPromise: promise });
    return promise;
  },
  
  clearAuth: () => {
    if (refreshTimer) {
      clearTimeout(refreshTimer);
      refreshTimer = null;
    }
    set({
      accessToken: null,
      expiresAt: null,
      user: null,
      isRefreshing: false,
      refreshPromise: null,
    });
  },
}));

function scheduleRefresh(expiresIn: number) {
  if (refreshTimer) {
    clearTimeout(refreshTimer);
  }
  
  // 提前 60 秒刷新
  const refreshAt = Math.max(0, (expiresIn - 60) * 1000);
  
  refreshTimer = window.setTimeout(() => {
    useAuthStore.getState().refreshToken().catch(console.error);
  }, refreshAt);
}
```

### 9.2 Axios 拦截器

```typescript
// api/request.ts
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores/authStore';

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  withCredentials: true, // 发送 HttpOnly Cookie (Refresh Token)
});

// 请求拦截器：添加 Authorization Header
request.interceptors.request.use((config) => {
  const { accessToken } = useAuthStore.getState();
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

// 响应拦截器：处理 401 自动刷新
request.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
    
    // 401 且未重试过 且不是刷新接口
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url?.includes('/auth/refresh')
    ) {
      originalRequest._retry = true;
      
      try {
        await useAuthStore.getState().refreshToken();
        
        // 重试原请求
        const { accessToken } = useAuthStore.getState();
        if (accessToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }
        return request(originalRequest);
      } catch (refreshError) {
        // 刷新失败，跳转登录
        useAuthStore.getState().clearAuth();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default request;
```

---

## 10. 迁移计划

### Phase 1: 基础设施 (5 天)

| 任务 | 负责人 | 状态 |
|------|--------|------|
| 添加 jjwt 依赖 | - | 待开始 |
| 实现 JwtProperties | - | 待开始 |
| 实现 JwtTokenProvider | - | 待开始 |
| 实现 RefreshTokenService | - | 待开始 |
| 实现 TokenBlacklistService | - | 待开始 |
| 更新 ehcache.xml | - | 待开始 |
| 单元测试 | - | 待开始 |

### Phase 2: 认证接口 (5 天)

| 任务 | 负责人 | 状态 |
|------|--------|------|
| 改造 AuthController.login | - | 待开始 |
| 实现 /auth/refresh | - | 待开始 |
| 改造 /auth/logout | - | 待开始 |
| 实现 JwtAuthenticationFilter | - | 待开始 |
| 实现 UserDeviceService | - | 待开始 |
| 集成测试 | - | 待开始 |

### Phase 3: 前端适配 (5 天)

| 任务 | 负责人 | 状态 |
|------|--------|------|
| 改造 Auth Store | - | 待开始 |
| 改造 Axios 拦截器 | - | 待开始 |
| 更新登录/登出流程 | - | 待开始 |
| E2E 测试 | - | 待开始 |

### Phase 4: 清理 (3 天)

| 任务 | 负责人 | 状态 |
|------|--------|------|
| 移除 Session 认证代码 | - | 待开始 |
| 移除 user_token 表逻辑 | - | 待开始 |
| 更新 API 文档 | - | 待开始 |
| 安全审计 | - | 待开始 |

---

## 11. 依赖配置

### 11.1 Maven 依赖

在 `adminpro-system/pom.xml` 中添加：

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

---

## 附录

### A. 错误码定义

| restCode | HTTP | 说明 |
|----------|------|------|
| 4001 | 401 | 用户名或密码错误 |
| 4002 | 401 | 验证码错误 |
| 4003 | 401 | 账户已锁定 |
| 4004 | 401 | 账户已停用 |
| 4005 | 401 | 设备数超限 |
| 4011 | 401 | Refresh Token 无效 |
| 4012 | 401 | Refresh Token 过期 |
| 4013 | 401 | Token 已被撤销 |

### B. 参考资料

- [RFC 7519 - JSON Web Token](https://datatracker.ietf.org/doc/html/rfc7519)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [OWASP JWT Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
