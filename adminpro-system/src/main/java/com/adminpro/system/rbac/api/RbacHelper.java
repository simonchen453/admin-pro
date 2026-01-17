package com.adminpro.system.rbac.api;

import com.adminpro.framework.base.util.SpringUtil;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.system.rbac.domains.entity.domain.DomainService;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.system.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.system.rbac.domains.entity.menu.MenuService;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignEntity;
import com.adminpro.system.rbac.domains.entity.userrole.UserRoleAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RBAC（基于角色的访问控制）辅助类
 * <p>
 * 本类提供RBAC权限管理相关的辅助功能，包括：
 * <ul>
 * <li>获取用户可访问的所有权限</li>
 * <li>获取用户可访问的角色ID列表</li>
 * <li>根据角色获取可访问的权限</li>
 * <li>查询所有域信息</li>
 * </ul>
 * <p>
 * 使用场景：
 * <ul>
 * <li>权限验证时查询用户权限</li>
 * <li>角色分配时获取用户角色</li>
 * <li>域管理中查询域列表</li>
 * </ul>
 *
 * @author simon
 * @since 2017/5/29
 */
@Service
public class RbacHelper {
    /**
     * 获取RBAC辅助类的实例
     * <p>
     * 通过Spring容器获取单例Bean实例，确保在整个应用中使用同一个对象
     *
     * @return RbacHelper的单例实例
     */
    public static RbacHelper getInstance() {
        return SpringUtil.getBean(RbacHelper.class);
    }

    @Autowired
    private UserRoleAssignService userRoleAssignService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private DomainService domainService;

    /**
     * 获取用户在指定域下可访问的所有权限编号
     * <p>
     * 该方法首先获取用户的所有角色ID（包括域通用角色和用户分配的角色），
     * 然后根据这些角色ID查询所有可访问的权限编号
     * <p>
     * 使用场景：
     * <ul>
     * <li>用户登录后加载权限列表</li>
     * <li>权限验证时检查用户是否有某权限</li>
     * <li>动态菜单生成</li>
     * </ul>
     *
     * @param userId     用户ID，不能为空
     * @param userDomain 用户域，不能为空
     * @return 用户可访问的权限编号数组，如果用户没有任何权限则返回空数组
     */
    public String[] getAccessibleAllPermissionsByUser(String userId, String userDomain) {
        List<String> privilegeNos = new ArrayList<>();

        String[] roleIds = getAccessibleRoleIds(userId, userDomain);
        List<String> ps = getAccessiblePermissionsByRoles(roleIds);
        for (int i = 0; i < ps.size(); i++) {
            privilegeNos.add(ps.get(i));
        }
        return privilegeNos.toArray(new String[privilegeNos.size()]);
    }

    /**
     * 获取用户在指定域下可访问的角色ID数组
     * <p>
     * 该方法返回用户在指定域下的所有角色ID，包括：
     * <ul>
     * <li>域的通用角色（如果配置了）：所有在该域下的用户自动拥有的角色</li>
     * <li>用户分配的角色：显式分配给该用户的角色</li>
     * </ul>
     * <p>
     * 使用场景：
     * <ul>
     * <li>权限查询时获取用户角色</li>
     * <li>角色管理中显示用户角色</li>
     * <li>数据权限控制</li>
     * </ul>
     *
     * @param userId     用户ID，不能为空
     * @param userDomain 用户域，不能为空
     * @return 角色ID数组，包含域通用角色和用户分配的角色
     */
    public String[] getAccessibleRoleIds(String userId, String userDomain) {
        List<UserRoleAssignEntity> list = userRoleAssignService.findByUserId(userId);
        List<String> result = new ArrayList<String>();
        UserDomainEnvEntity domainEnvEntity = UserDomainEnvService.getInstance().findByUserDomain(userDomain);
        if (domainEnvEntity != null && StringHelper.isNotEmpty(domainEnvEntity.getCommonRole())) {
            com.adminpro.system.rbac.domains.entity.role.RoleEntity roleEntity = com.adminpro.system.rbac.domains.entity.role.RoleService
                    .getInstance().findByName(domainEnvEntity.getCommonRole());
            if (roleEntity != null) {
                result.add(roleEntity.getId());
            }
        }

        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).getRoleId());
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * 根据角色ID列表获取可访问的权限列表
     * <p>
     * 这是一个私有辅助方法，用于查询指定角色列表所拥有的所有权限
     * <p>
     * 注意：该方法返回的是权限编号列表，可能包含重复的权限编号
     *
     * @param roleIds 角色ID数组，不能为空
     * @return 权限编号列表，可能包含重复项
     */
    private List<String> getAccessiblePermissionsByRoles(String[] roleIds) {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < roleIds.length; i++) {
            List<String> permission = menuService.findPermissionByRoleId(roleIds[i]);
            permissionList.addAll(permission);
        }
        return permissionList;
    }

    /**
     * 查询所有域信息
     * <p>
     * 获取系统中配置的所有域（Domain）实体列表
     * <p>
     * 使用场景：
     * <ul>
     * <li>域管理页面显示所有域</li>
     * <li>用户注册时选择所属域</li>
     * <li>多租户管理</li>
     * </ul>
     *
     * @return 所有域实体列表，如果系统没有配置域则返回空列表
     */
    public List<DomainEntity> findAllDomains() {
        List<DomainEntity> domains = domainService.findAll();
        return domains;
    }
}
