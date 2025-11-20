package com.adminpro.web;

import com.adminpro.framework.common.BaseRoutingController;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.framework.security.auth.LoginUser;
import com.adminpro.rbac.api.LoginHelper;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvEntity;
import com.adminpro.rbac.domains.entity.domain.UserDomainEnvService;
import com.adminpro.tools.domains.entity.session.SessionEntity;
import com.adminpro.tools.domains.entity.session.SessionService;
import com.adminpro.tools.domains.enums.SessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@RequestMapping
public class IndexController extends BaseRoutingController {

    @Autowired
    private UserDomainEnvService userDomainEnvService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            UserDomainEnvEntity domainEnvEntity = UserDomainEnvService.getInstance().findByUserDomain(loginUser.getUserDomain());
            String homePageUrl = domainEnvEntity.getHomePageUrl();
            logger.debug("forward to: " + homePageUrl);
            return "forward:" + homePageUrl;
        }
        String loginContinueUrl = WebHelper.getLoginContinueUrl(request);
        if (!StringHelper.isEmpty(loginContinueUrl)) {
            WebHelper.setLoginContinueUrl(request, loginContinueUrl);
        }

        String loginUrl = ConfigHelper.getString(BaseConstants.DEFAULT_LOGIN_URL_KEY, "/login");
        logger.debug("forward to: " + loginUrl);
        return "forward:" + loginUrl;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String indexhome() {
        LoginUser loginUser = LoginHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            UserDomainEnvEntity domainEnvEntity = UserDomainEnvService.getInstance().findByUserDomain(loginUser.getUserDomain());
            String homePageUrl = domainEnvEntity.getHomePageUrl();
            logger.debug("forward to: " + homePageUrl);
            return "forward:" + homePageUrl;
        }
        String loginUrl = ConfigHelper.getString(BaseConstants.DEFAULT_LOGIN_URL_KEY, "/login");
        logger.debug("forward to: " + loginUrl);
        return "forward:" + loginUrl;
    }

    @PreAuthorize("@ss.hasPermission('system:home')")
    @RequestMapping(value = "/admin/home", method = RequestMethod.GET)
    public String home() {
        prepareData();
        return "admin/home";
    }

    @RequestMapping(value = "/sessionExpired", method = RequestMethod.GET)
    public String sessionExpired() throws IOException {
        prepareData();
        SessionEntity sessionEntity = SessionService.getInstance().findBySessionId(WebHelper.getSessionId());
        String loginContinueUrl = WebHelper.getLoginContinueUrl(request);
        if (!StringHelper.isEmpty(loginContinueUrl)) {
            WebHelper.setLoginContinueUrl(request, loginContinueUrl);
        }
        if (sessionEntity == null) {
            return "redirect:/?" + WebHelper.LOGIN_CONTINUE_URL + "=" + loginContinueUrl;
        } else {
            if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.EXPIRE.getCode())) {
                return "admin/sessionExpired";
            } else if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.KILLED.getCode())) {
                return "redirect:/sessionTerminate?" + WebHelper.LOGIN_CONTINUE_URL + "=" + loginContinueUrl;
            } else {
                return "redirect:/?" + WebHelper.LOGIN_CONTINUE_URL + "=" + loginContinueUrl;
            }
        }
    }

    @RequestMapping(value = "/sessionTerminate", method = RequestMethod.GET)
    public String sessionTerminate() throws IOException {
        prepareData();
        SessionEntity sessionEntity = SessionService.getInstance().findBySessionId(WebHelper.getSessionId());
        String loginContinueUrl = WebHelper.getLoginContinueUrl(request);
        if (!StringHelper.isEmpty(loginContinueUrl)) {
            WebHelper.setLoginContinueUrl(request, loginContinueUrl);
        }
        if (sessionEntity == null) {
            return "redirect:/?" + WebHelper.LOGIN_CONTINUE_URL + "=" + loginContinueUrl;
        } else {
            if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.EXPIRE.getCode())) {
                return "redirect:/sessionExpired?" + WebHelper.LOGIN_CONTINUE_URL + "=" + loginContinueUrl;
            } else if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.KILLED.getCode())) {
                return "admin/sessionTerminate";
            } else {
                return "redirect:/?" + WebHelper.LOGIN_CONTINUE_URL + "=" + loginContinueUrl;
            }
        }
    }

    @PreAuthorize("@ss.hasPermission('system:changepwd')")
    @RequestMapping(value = "/changepwd", method = RequestMethod.GET)
    public String changepwd() {
        prepareData();
        return "admin/common/changepwd";
    }

    @PreAuthorize("@ss.hasPermission('system:swagger')")
    @RequestMapping(value = "/admin/swagger", method = RequestMethod.GET)
    public String swagger() {
        prepareData();
        return "admin/swagger/swagger.html";
    }
}
