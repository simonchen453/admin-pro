package com.adminpro.framework.security.handle;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.rbac.domains.entity.user.UserEntity;
import com.adminpro.web.BaseConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义退出处理类 返回成功
 *
 * @author simon
 */
@Configuration
public class LogoutHandlerImpl implements LogoutHandler {

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        String continueUrl = WebHelper.getLoginContinueUrl(request);
        if (StringHelper.isNotNull(loginUser)) {
            UserEntity userEntity = LoginHelper.getInstance().getUserEntity();
            UserDomainEnvEntity domainEnv = userDomainEnvService.findByUserDomain(userEntity.getUserDomain());
            if (domainEnv != null) {
                String loginUrl = ConfigHelper.getString(BaseConstants.DEFAULT_LOGIN_URL_KEY);
                String url = domainEnv.getLoginUrl();
                loginUrl = StringUtils.isNotEmpty(url) ? url : loginUrl;
                continueUrl = loginUrl;
            } else {
                continueUrl = "/";
            }
        }

        request.setAttribute("continueUrl", continueUrl);
    }
}
