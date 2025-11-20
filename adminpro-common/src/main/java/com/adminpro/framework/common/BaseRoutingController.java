package com.adminpro.framework.common;

import com.adminpro.core.base.util.DateUtil;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.common.web.BaseController;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.common.RbacCacheConstants;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.rbac.domains.entity.dept.DeptEntity;
import com.adminpro.rbac.domains.entity.dept.DeptService;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.rbac.domains.entity.menu.MenuEntity;
import com.adminpro.rbac.domains.entity.menu.MenuService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.rbac.domains.vo.menu.MenuTreeVo;
import com.adminpro.tools.domains.entity.dict.DictDataEntity;
import com.adminpro.tools.domains.entity.dict.DictEntity;
import com.adminpro.tools.domains.entity.dict.DictService;
import com.adminpro.web.BaseConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author simon
 */
public abstract class BaseRoutingController extends BaseController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    protected void prepareData() {
        HttpSession session = request.getSession();
        UserEntity userEntity = LoginHelper.getInstance().getUserEntity();
        String loginUrl = ConfigHelper.getString(BaseConstants.DEFAULT_LOGIN_URL_KEY);
        if (userEntity != null) {
            List<MenuTreeVo> res = (List<MenuTreeVo>) session.getAttribute(RbacCacheConstants.SESSION_MENU_CACHE);
            List<String> parentMenuIds = (List<String>) session.getAttribute(RbacCacheConstants.SESSION_MENU_PARENT_CACHE);

            if (res == null || parentMenuIds == null) {
                List<MenuEntity> menuEntityList = menuService.findMenuTreeByUserIden(userEntity.getUserIden());
                res = menuService.buildMenus(menuEntityList);
                parentMenuIds = menuService.getParentMenuIds(menuEntityList);
                session.setAttribute(RbacCacheConstants.SESSION_MENU_CACHE, res);
                session.setAttribute(RbacCacheConstants.SESSION_MENU_PARENT_CACHE, parentMenuIds);
            }
            String avatarUrl = "";
            if (userEntity != null) {
                avatarUrl = userEntity.getAvatarUrl();
            }
            if (StringUtils.isEmpty(avatarUrl)) {
                avatarUrl = request.getContextPath() + "/img/avatar.png";
            }
            request.setAttribute("menus", res);
            request.setAttribute("date", DateUtil.formatDate(new Date()));
            request.setAttribute("week", DateUtil.getWeekOfDate(new Date()));
            request.setAttribute("realName", userEntity.getRealName());
            request.setAttribute("avatarUrl", avatarUrl);
            String activeMenuId = (String) request.getSession().getAttribute(RbacConstants.MENU_SESSION_KEY);
            request.setAttribute("default_opened", new String[]{getTopParentMenuId(activeMenuId, res)});
            if (userEntity != null) {
                if (userEntity.getLatestLoginTime() != null) {
                    request.setAttribute("latest_login_time", DateUtil.formatDateTime(userEntity.getLatestLoginTime()));
                }
                String deptNo = userEntity.getDeptNo();
                DeptEntity deptEntity = DeptService.getInstance().findByNo(deptNo);
                if (deptEntity != null) {
                    request.setAttribute("dept", deptEntity);
                }
            }
            UserDomainEnvEntity domainEnv = userDomainEnvService.findByUserDomain(userEntity.getUserDomain());
            if (domainEnv != null) {
                String url = domainEnv.getLoginUrl();
                loginUrl = StringUtils.isNotEmpty(url) ? url : loginUrl;
            }
        }
        String releaseVersion = ConfigHelper.getString(BaseConstants.SERVER_RELEASE_VERSION_KEY);
        if(StringUtils.isNotEmpty(releaseVersion)) {
            DictEntity dictEntity = DictService.getInstance().findByKey(releaseVersion);
            if(dictEntity != null){
                List<String> changeLogs = dictEntity.getData().stream()
                        .map(DictDataEntity::getLabel)
                        .collect(Collectors.toList());
                request.setAttribute("change_logs", changeLogs);
            }else{
                request.setAttribute("change_logs", new ArrayList<String>());
            }
        }

        request.setAttribute("platform_name", ConfigHelper.getString(BaseConstants.PLATFORM_NAME_KEY));
        request.setAttribute("platform_short_name", ConfigHelper.getString(BaseConstants.PLATFORM_SHORT_NAME_KEY));
        request.setAttribute("server_ip", ConfigHelper.getString(BaseConstants.SERVER_IP_KEY));
        request.setAttribute("server_port", ConfigHelper.getString(BaseConstants.SERVER_PORT_KEY));
        request.setAttribute("build_version", ConfigHelper.getString(BaseConstants.SERVER_BUILD_VERSION_KEY));
        request.setAttribute("release_version", releaseVersion);

        request.setAttribute("login_url", loginUrl);
        request.setAttribute("copy_right", ConfigHelper.getString(BaseConstants.COPY_RIGHT_KEY));
        String ipAddr = WebHelper.getIpAddr(request);
        request.setAttribute("login_ip_address", ipAddr);
        String[] permissions = LoginHelper.getInstance().getPermissions();
        request.setAttribute("permissions", permissions);
    }

    private String getTopParentMenuId(String menuId, List<MenuTreeVo> res) {
        for (int i = 0; i < res.size(); i++) {
            MenuTreeVo menuTreeVo = res.get(i);
            List<MenuTreeVo> subs = menuTreeVo.getSubs();
            if (subs != null && subs.size() > 0) {
                boolean included = isIncluded(menuId, subs);
                if (included) {
                    return menuTreeVo.getId();
                }
            }
        }
        return "";
    }

    private boolean isIncluded(String menuId, List<MenuTreeVo> res) {
        for (int i = 0; i < res.size(); i++) {
            MenuTreeVo menuTreeVo = res.get(i);
            if (StringHelper.equals(menuTreeVo.getId(), menuId)) {
                return true;
            } else {
                List<MenuTreeVo> subs = menuTreeVo.getSubs();
                if (subs != null && subs.size() > 0) {
                    return isIncluded(menuId, subs);
                }
            }
        }
        return false;
    }
}
