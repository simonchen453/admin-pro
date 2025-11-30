package com.adminpro.framework.filters;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.rbac.common.RbacConstants;
import com.adminpro.tools.domains.entity.session.SessionEntity;
import com.adminpro.tools.domains.entity.session.SessionService;
import com.adminpro.tools.domains.enums.SessionStatus;
import com.adminpro.web.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


/**
 * @author simon
 * @date 2020/6/18
 */
@Component
public class SessionFilter implements Filter {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean flag = true;
        boolean ignore = checkWhiteList();
        String contextPath = WebHelper.getContextPath();
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //将当前激活的menuId
        String menuId = servletRequest.getParameter(RbacConstants.MENU_SESSION_KEY);
        if (StringHelper.isNotEmpty(menuId)) {
            request.getSession().setAttribute(RbacConstants.MENU_SESSION_KEY, menuId);
        }
        if (!ignore && flag) {
            String url = WebHelper.getRequestUriWithoutContextPath(request);
            WebHelper.setLoginContinueUrl(request, url);

            //判断是否session过期
            boolean newSession = isNewSession();
            if (newSession) {

                WebHelper.redirect(contextPath + "/sessionExpired?" + WebHelper.LOGIN_CONTINUE_URL + "=" + url);
                logger.debug("Session是已过期，跳转到sessionExpired");
                flag = false;
            } else {
                SessionEntity sessionEntity = SessionService.getInstance().findBySessionId(WebHelper.getSessionId());
                if (sessionEntity != null) {
                    if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.EXPIRE.getCode())) {
                        WebHelper.redirect(contextPath + "/sessionExpired?" + WebHelper.LOGIN_CONTINUE_URL + "=" + url);
                        flag = false;
                    } else if (StringHelper.equals(sessionEntity.getStatus(), SessionStatus.KILLED.getCode())) {
                        WebHelper.redirect(contextPath + "/sessionTerminate?" + WebHelper.LOGIN_CONTINUE_URL + "=" + url);
                        flag = false;
                    }
                }
            }
        }
        if (flag) {
            //执行
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isNewSession() {
        /*HttpServletRequest request = WebHelper.getHttpRequest();
        if (request != null) {
            HttpSession session = request.getSession();
            if (session != null) {
                return session.isNew();
            }
        }*/

        return false;
    }

    private boolean checkWhiteList() {
        String[] whiteList = ConfigHelper.getStringArray(BaseConstants.SESSION_FILTER_WHITE_LIST_KEY);
        HttpServletRequest request = WebHelper.getHttpRequest();
        if (request == null) {
            return false;
        }
        PathPatternParser parser = PathPatternParser.defaultInstance;
        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
            requestPath = requestPath.substring(contextPath.length());
        }
        PathContainer pathContainer = PathContainer.parsePath(requestPath);
        for (int i = 0; i < whiteList.length; i++) {
            try {
                PathPattern pattern = parser.parse(whiteList[i]);
                if (pattern.matches(pathContainer)) {
                    return true;
                }
            } catch (Exception e) {
                logger.debug("Failed to match path pattern: {}", whiteList[i], e);
            }
        }
        return false;
    }
}
