package com.adminpro.framework.security.handle;

import com.adminpro.core.base.entity.R;
import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.framework.common.helper.WebHelper;
import com.adminpro.web.BaseConstants;
import com.adminpro.core.base.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类，返回未授权
 *
 * @author simon
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        int code = HttpStatus.UNAUTHORIZED.value();
        String msg = StringHelper.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        boolean restRequest = WebHelper.isRestRequest();
        boolean ajaxRequest = WebHelper.isAjaxRequest(request);
        String url = WebHelper.getRequestUriWithoutContextPath(request);
        logger.error(msg, e);
        if (ajaxRequest || restRequest) {
            WebHelper.renderString(response, JsonUtil.toJson(R.error(String.valueOf(code), msg)));
        } else {
            String sessionNotExist = ConfigHelper.getString(BaseConstants.URL_SESSION_DOES_NOT_EXIST_KEY);
            WebHelper.redirect(request.getContextPath() + sessionNotExist + "?" + WebHelper.LOGIN_CONTINUE_URL + "=" + url);
        }
    }
}
