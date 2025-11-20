package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.jdbc.query.IModelConverter;
import com.adminpro.rbac.domains.entity.role.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class ListRoleVoConverter implements IModelConverter<RoleEntity, ListRoleVo> {

    @Override
    public ListRoleVo convert(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        ListRoleVo listRoleVo = new ListRoleVo();
        listRoleVo.setId(entity.getId());
        listRoleVo.setName(entity.getName());
        listRoleVo.setDisplay(entity.getDisplay());
        listRoleVo.setSystem(entity.isSystem());
        listRoleVo.setStatus(entity.getStatus());
        return listRoleVo;
    }

    @Override
    public RoleEntity inverse(ListRoleVo s) {
        return null;
    }

}
