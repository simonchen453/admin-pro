package com.adminpro.system.config;

import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.tools.domains.entity.exceptionlog.ExceptionLogEntity;
import com.adminpro.system.tools.domains.entity.exceptionlog.ExceptionLogService;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Created by simon on 2017/6/9.
 */
@RestController
public class ErrorBasicController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorBasicController.class);

    public static final String ERROR_PATH = "/error";
    public static final String SHOW_EXCEPTION = "egp.errors.show.exception";

    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    private ErrorAttributes errorAttributes;

    @Autowired
    private ExceptionLogService exceptionLogService;

    @RequestMapping(value = ERROR_PATH)
    public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> errorAttributes = getErrorAttributes(request, errorAttributeOptions);
        logException(errorAttributes);

        // Remove trace and exception before sending to client
        errorAttributes.remove("trace");
        errorAttributes.remove("exception");

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new Gson().toJson(errorAttributes));
    }

    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public void errorHtml(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String errorPage = ConfigHelper.getString("egp.error.page");
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> errorAttributes = getErrorAttributes(request, errorAttributeOptions);
        logException(errorAttributes);
        request.setAttribute("errors", errorAttributes);

        if (!StringHelper.isEmpty(errorPage)) {
            request.getRequestDispatcher(errorPage).forward(request, response);
        } else {
            boolean showException = ConfigHelper.getBoolean(SHOW_EXCEPTION, false);
            Integer status = (Integer) errorAttributes.get("status");
            String path = (String) errorAttributes.get("path");
            String trace = (String) errorAttributes.get("trace");
            String message = (String) errorAttributes.get("message");
            String error = (String) errorAttributes.get("error");
            HtmlCanvas canvas = new HtmlCanvas();
            String resourcePath = request.getContextPath();
            canvas.meta(writer -> {
                writer.write(" http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"");
            })
                    .link(HtmlAttributesFactory.href(resourcePath + "/css/error-page.css").rel("stylesheet")
                            .type("text/css"))
                    .script(HtmlAttributesFactory.src(resourcePath + "/plugins/jquery/jquery.min.js"))
                    ._script()
                    .script(HtmlAttributesFactory.type("text/javascript"))
                    .write("\n$(function () {\n")
                    .write("$('#title_head').click(function () {\n")
                    .write("$('#divError').toggle();\n")
                    .write("})\n")
                    .write("})\n")
                    ._script()
                    .div()
                    .div()
                    .div(HtmlAttributesFactory
                            .class_("fb-rounded-box-content-error-Appexception fb-summary-info-error-Appexception"))
                    .div()
                    .div(HtmlAttributesFactory.id("title_head"))
                    .strong().write("An unexpected error has been encountered")
                    ._strong()
                    ._div()
                    .br()
                    .div(HtmlAttributesFactory.id("exception_content"))
                    .write(status)
                    .write("-")
                    .write("-")
                    .write(message)
                    .write(", Path: ")
                    .write(path)
                    .br();
            if (showException) {
                canvas.div(HtmlAttributesFactory.id("divError").style("display:none;"))
                        .write(trace).br().br()
                        ._div();
            }
            canvas._div()
                    ._div()
                    .br()
                    .hr()
                    .div(HtmlAttributesFactory.class_("fb-summary-info-error-Appexception-action"))
                    .write("We are sorry for the error you have encountered.").br()
                    .write("Please report it to your administrator and we will investigate it.")
                    ._div()
                    ._div()
                    ._div()
                    ._div();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            response.getWriter().write(canvas.toHtml());
        }
    }

    private void logException(Map<String, Object> errorAttributes) {
        String status = (String) errorAttributes.get("restCode");
        String path = (String) errorAttributes.get("path");
        String message = (String) errorAttributes.get("message");
        String trace = (String) errorAttributes.get("trace");
        String exception = (String) errorAttributes.get("exception");

        ExceptionLogEntity log = new ExceptionLogEntity();

        // Use full stack trace for details
        StringBuilder details = new StringBuilder();
        if (message != null) {
            details.append("Message: ").append(message).append("\n");
        }
        if (trace != null) {
            details.append(trace);
        }
        log.setDetails(details.toString());

        // Use exception class name as type, fallback to message or "Unknown Error"
        if (exception != null) {
            log.setType(exception);
        } else if (message != null) {
            log.setType(StringUtils.abbreviate(message, 255));
        } else {
            log.setType("Unknown Error");
        }

        log.setPath(path);
        try {
            exceptionLogService.create(log);
        } catch (Exception e) {
            logger.error("记录异常日志失败: path={}, message={}", path, message, e);
        }
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions errorAttributeOptions) {
        WebRequest webRequest = new ServletWebRequest(request);
        return errorAttributes.getErrorAttributes(webRequest, errorAttributeOptions);
    }
}
