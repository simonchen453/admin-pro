package com.adminpro.system.core.security.service;

import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.core.common.helper.WebHelper;
import com.adminpro.system.core.security.auth.LoginUser;
import com.adminpro.system.rbac.api.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 权限服务
 * <p>
 * 提供权限验证的核心功能，支持Spring Security注解式权限控制。
 * Bean名称为"ss"（取自Spring Security首字母），可以在SpEL表达式中使用。
 * <p>
 * 使用示例：
 * <pre>
 * // 在Controller方法上使用注解
 * &#64;PreAuthorize("@ss.hasPermission('system:user:add')")
 * public void addUser() { ... }
 *
 * // 在Service中手动调用
 * if (permissionService.hasPermission("system:user:edit")) {
 *     // 执行需要权限的操作
 * }
 * </pre>
 * <p>
 * 权限格式：
 * <pre>
 * 模块:功能:操作（例如：system:user:add, system:user:edit）
 * </pre>
 * <p>
 * 特殊权限：
 * <ul>
 * <li>*:*:* - 超级管理员权限，拥有所有权限</li>
 * </ul>
 * <p>
 * 开发模式：在开发模式下，所有权限验证都返回true
 *
 * @author simon
 * @see org.springframework.security.access.prepost.PreAuthorize
 */
@Service("ss")
public class PermissionService {
    /**
     * 所有权限标识
     * <p>
     * 拥有此权限的用户可以访问任何资源，通常用于超级管理员
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    private static final String SUPER_ADMIN = "admin";

    /**
     * 角色分隔符
     */
    private static final String ROLE_DELIMITER = ",";

    /**
     * 权限分隔符
     */
    private static final String PERMISSION_DELIMITER = ",";

    /**
     * 验证用户是否具备某权限
     * <p>
     * 检查当前登录用户是否拥有指定的权限。
     * 如果用户拥有"所有权限"标识（*:*:*），则直接返回true。
     * <p>
     * 使用场景：
     * <ul>
     * <li>在Controller方法上使用@PreAuthorize注解</li>
     * <li>在业务逻辑中手动检查权限</li>
     * </ul>
     *
     * @param permission 权限字符串，格式：模块:功能:操作
     * @return true表示用户具备该权限，false表示不具备
     */
    public boolean hasPermission(String permission) {
        if (StringHelper.isEmpty(permission)) {
            return false;
        }
        if (WebHelper.isDevModel()) {
            return true;
        }
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (StringHelper.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 验证用户是否不具备某权限
     * <p>
     * 与hasPermission逻辑相反，用于"不具备某权限时才能访问"的场景。
     *
     * @param permission 权限字符串，格式：模块:功能:操作
     * @return true表示用户不具备该权限，false表示具备
     */
    public boolean lacksPermission(String permission) {
        return hasPermission(permission) != true;
    }

    /**
     * 验证用户是否具有以下任意一个权限
     * <p>
     * 当用户拥有指定权限列表中的任意一个时即返回true。
     * 权限列表使用逗号分隔。
     * <p>
     * 使用场景：
     * <ul>
     * <li>用户只要有查看或编辑权限就可以访问</li>
     * <li>多个角色共有的权限</li>
     * </ul>
     *
     * @param permissions 权限列表，使用逗号分隔
     *                    例如："system:user:add,system:user:edit"
     * @return true表示用户至少拥有其中一个权限，false表示都不具备
     */
    public boolean hasAnyPermission(String permissions) {
        if (StringHelper.isEmpty(permissions)) {
            return false;
        }
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (StringHelper.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        List<String> authorities = loginUser.getPermissions();
        for (String permission : permissions.split(PERMISSION_DELIMITER)) {
            if (permission != null && hasPermissions(authorities, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含权限（内部方法）
     * <p>
     * 检查用户的权限列表中是否包含指定权限。
     * 如果用户拥有"所有权限"标识（*:*:*），则直接返回true。
     *
     * @param permissions 用户权限列表
     * @param permission  要检查的权限字符串
     * @return true表示包含该权限，false表示不包含
     */
    private boolean hasPermissions(List<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringHelper.trim(permission));
    }
}
