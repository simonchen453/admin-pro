package com.adminpro.framework.security.service;

import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.rbac.api.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * RuoYi首创 自定义权限实现，ss取自SpringSecurity首字母
 *
 * @author simon
 */
@Service("ss")
public class PermissionService {
    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识
     */
    private static final String SUPER_ADMIN = "admin";

    private static final String ROLE_DELIMITER = ",";

    private static final String PERMISSION_DELIMITER = ",";

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
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
     * 验证用户是否不具备某权限，与 hasPermission逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPermission(String permission) {
        return hasPermission(permission) != true;
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_NAMES_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
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
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(List<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringHelper.trim(permission));
    }
}
