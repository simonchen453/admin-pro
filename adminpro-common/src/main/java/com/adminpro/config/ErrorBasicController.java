package com.adminpro.config;

import com.adminpro.framework.common.helper.ConfigHelper;
import com.adminpro.framework.common.helper.StringHelper;
import com.adminpro.tools.domains.entity.exceptionlog.ExceptionLogEntity;
import com.adminpro.tools.domains.entity.exceptionlog.ExceptionLogService;
import com.google.gson.Gson;
import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by simon on 2017/6/9.
 */
@Controller
public class ErrorBasicController implements ErrorController {
    public static final String ERROR_PATH = "/error";
    public static final String SHOW_EXCEPTION = "egp.errors.show.exception";


    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> errorAttributes = getErrorAttributes(request, errorAttributeOptions);
        logException(errorAttributes);

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new Gson().toJson(errorAttributes));
    }

    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public void errorHtml(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String errorPage = ConfigHelper.getString("egp.error.page");
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> errorAttributes = getErrorAttributes(request, errorAttributeOptions);
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
                    .link(HtmlAttributesFactory.href(resourcePath + "/css/error-page.css").rel("stylesheet").type("text/css"))
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
                    .div(HtmlAttributesFactory.class_("fb-rounded-box-content-error-Appexception fb-summary-info-error-Appexception"))
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

        ExceptionLogEntity log = new ExceptionLogEntity();
        log.setDetails(trace);
        log.setType(message);
        log.setPath(path);
        try {
            ExceptionLogService.getInstance().create(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions errorAttributeOptions) {
        WebRequest webRequest = new ServletWebRequest(request);
        return errorAttributes.getErrorAttributes(webRequest, errorAttributeOptions);
    }
}
