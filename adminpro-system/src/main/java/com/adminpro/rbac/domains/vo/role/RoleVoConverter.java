package com.adminpro.rbac.domains.vo.role;

import com.adminpro.core.jdbc.query.IModelConverter;
import com.adminpro.rbac.domains.entity.role.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleVoConverter implements IModelConverter<RoleEntity, RoleVo> {

    @Override
    public RoleVo convert(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        RoleVo roleVo = new RoleVo();
        roleVo.setId(entity.getId());
        roleVo.setName(entity.getName());
        roleVo.setDisplay(entity.getDisplay());
        return roleVo;
    }

    @Override
    public RoleEntity inverse(RoleVo s) {
        if (s == null) {
            return null;
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(s.getId());
        roleEntity.setName(s.getName());
        roleEntity.setDisplay(s.getDisplay());
        return roleEntity;
    }

}
