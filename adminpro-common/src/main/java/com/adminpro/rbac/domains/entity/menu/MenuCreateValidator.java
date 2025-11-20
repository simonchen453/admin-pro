package com.adminpro.rbac.domains.entity.menu;

import com.adminpro.core.base.message.MessageBundle;
import com.adminpro.core.base.validator.BaseValidator;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.rbac.enums.MenuType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 菜单权限 校验类
 *
 * @author simon
 * @date 2020-05-21
 */
@Component
public class MenuCreateValidator extends BaseValidator<MenuEntity> {

    @Autowired
    private MenuService menuService;

    /**
     * 校验创建菜单权限
     */
    @Override
    public void validate(MenuEntity entity, MessageBundle msgBundle) {
        super.baseValidate(entity, msgBundle);
        if (!msgBundle.hasErrorMessage("name")) {
            MenuEntity menuEntity = menuService.findByName(entity.getName());
            if (menuEntity != null && !StringHelper.equals(menuEntity.getId(), entity.getId())) {
                msgBundle.addErrorMessage("name", "菜单名称不能重复");
            }
        }
        if (!msgBundle.hasErrorMessage("parentId")) {
            if (StringUtils.isEmpty(entity.getParentId())) {
                MenuEntity menuEntity = menuService.findById(entity.getParentId());
                if (menuEntity == null) {
                    msgBundle.addErrorMessage("parentId", "父级菜单不存在");
                }
            }
        }
        if (!msgBundle.hasErrorMessage("orderNum")) {
            Integer orderNum = entity.getOrderNum();
            if (orderNum == null || orderNum <= 0) {
                msgBundle.addErrorMessage("orderNum", "显示顺序必须大于0");
            }
        }
        if (!msgBundle.hasErrorMessage("url")) {
            String type = entity.getType();
            if (StringUtils.equals(type, MenuType.MENU.getCode()) && StringUtils.isEmpty(entity.getUrl())) {
                msgBundle.addErrorMessage("url", "链接url不能为空");
            }
        }
        if (!msgBundle.hasErrorMessage("type")) {
            if (!MenuType.isValidCode(entity.getType())) {
                msgBundle.addErrorMessage("type", "菜单类型不合法");
            }
        }
        if (!msgBundle.hasErrorMessage("permission")) {
            String type = entity.getType();
            if (!StringUtils.equals(type, MenuType.CATALOG.getCode()) && StringUtils.isEmpty(entity.getPermission())) {
                msgBundle.addErrorMessage("permission", "权限标识不能为空");
            }
        }
    }
}
