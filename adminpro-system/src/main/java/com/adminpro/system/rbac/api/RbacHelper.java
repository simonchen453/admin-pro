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
 * Created by simon on 2017/5/29.
 */
@Service
public class RbacHelper {
    public static RbacHelper getInstance() {
        return SpringUtil.getBean(RbacHelper.class);
    }

    @Autowired
    private UserRoleAssignService userRoleAssignService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private DomainService domainService;

    public String[] getAccessibleAllPermissionsByUser(String userId, String userDomain) {
        List<String> privilegeNos = new ArrayList<>();

        String[] roleIds = getAccessibleRoleIds(userId, userDomain);
        List<String> ps = getAccessiblePermissionsByRoles(roleIds);
        for (int i = 0; i < ps.size(); i++) {
            privilegeNos.add(ps.get(i));
        }
        return privilegeNos.toArray(new String[privilegeNos.size()]);
    }

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

    private List<String> getAccessiblePermissionsByRoles(String[] roleIds) {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < roleIds.length; i++) {
            List<String> permission = menuService.findPermissionByRoleId(roleIds[i]);
            permissionList.addAll(permission);
        }
        return permissionList;
    }

    public List<DomainEntity> findAllDomains() {
        List<DomainEntity> domains = domainService.findAll();
        return domains;
    }
}
