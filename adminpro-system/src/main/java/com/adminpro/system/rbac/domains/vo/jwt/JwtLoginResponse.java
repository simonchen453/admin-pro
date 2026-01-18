package com.adminpro.system.rbac.domains.vo.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * JWT 登录响应
 * 
 * @author adminpro
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "JWT 登录响应")
public class JwtLoginResponse {

    @Schema(description = "Access Token")
    private String accessToken;

    @Schema(description = "Refresh Token")
    private String refreshToken;

    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access Token 过期时间(秒)")
    private long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo user;

    @Data
    @Builder
    @Schema(description = "简要用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private String id;

        @Schema(description = "登录名")
        private String loginName;

        @Schema(description = "真实姓名")
        private String realName;

        @Schema(description = "头像URL")
        private String avatarUrl;

        @Schema(description = "角色列表")
        private List<String> roles;
    }
}
