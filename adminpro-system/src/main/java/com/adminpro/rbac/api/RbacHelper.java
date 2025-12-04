package com.adminpro.rbac.api;

import com.adminpro.core.base.util.SpringUtil;
import com.adminpro.rbac.domains.entity.domain.DomainEntity;
import com.adminpro.rbac.domains.entity.domain.DomainService;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.rbac.domains.entity.menu.MenuService;
import com.adminpro.rbac.domains.entity.user.UserIden;
import com.adminpro.rbac.domains.entity.userrole.UserRoleAssignEntity;
import com.adminpro.rbac.domains.entity.userrole.UserRoleAssignService;
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

    public String[] getAccessibleAllPermissionsByUser(UserIden uid) {
        List<String> privilegeNos = new ArrayList<>();

        String[] roleNames = getAccessibleRoleNames(uid);
        List<String> ps = getAccessiblePermissionsByRoles(roleNames);
        for (int i = 0; i < ps.size(); i++) {
            privilegeNos.add(ps.get(i));
        }
        return privilegeNos.toArray(new String[privilegeNos.size()]);
    }

    public String[] getAccessibleRoleNames(UserIden uid) {
        List<UserRoleAssignEntity> list = userRoleAssignService.findByUserIden(uid);
        List<String> result = new ArrayList<String>();
        UserDomainEnvEntity domainEnvEntity = UserDomainEnvService.getInstance().findByUserDomain(uid.getUserDomain());
        if (domainEnvEntity != null) {
            result.add(domainEnvEntity.getCommonRole());
        }

        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).getRoleName());
        }
        return result.toArray(new String[result.size()]);
    }

    private List<String> getAccessiblePermissionsByRoles(String[] roleNames) {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < roleNames.length; i++) {
            List<String> permission = menuService.findPermissionByRoleName(roleNames[i]);
            permissionList.addAll(permission);
        }
        return permissionList;
    }

    public List<DomainEntity> findAllDomains() {
        List<DomainEntity> domains = domainService.findAll();
        return domains;
    }
}
