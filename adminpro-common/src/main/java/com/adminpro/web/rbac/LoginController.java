package com.adminpro.web.rbac;

import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/login")
public class LoginController extends BaseRoutingController {

    @RequestMapping(method = RequestMethod.GET)
    public String login() {
        prepareData();
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            UserDomainEnvEntity domainEnvEntity = UserDomainEnvService.getInstance().findByUserDomain(loginUser.getUserDomain());
            String homePageUrl = domainEnvEntity.getHomePageUrl();
            logger.debug("forward to: " + homePageUrl);
            return "forward:" + homePageUrl;
        }
        String loginContinueUrl = WebHelper.getLoginContinueUrl(request);
        if (StringUtils.isNotEmpty(loginContinueUrl)) {
            WebHelper.setLoginContinueUrl(request, loginContinueUrl);
        }
        return "login";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminlogin() {
        prepareData();
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            UserDomainEnvEntity domainEnvEntity = UserDomainEnvService.getInstance().findByUserDomain(loginUser.getUserDomain());
            String homePageUrl = domainEnvEntity.getHomePageUrl();
            logger.debug("forward to: " + homePageUrl);
            return "forward:" + homePageUrl;
        }
        String loginContinueUrl = WebHelper.getLoginContinueUrl(request);
        if (StringUtils.isNotEmpty(loginContinueUrl)) {
            WebHelper.setLoginContinueUrl(request, loginContinueUrl);
        }
        return "adminlogin";
    }
}
