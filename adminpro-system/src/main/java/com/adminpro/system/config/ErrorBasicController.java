package com.adminpro.system.config;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.helper.ConfigHelper;
import com.adminpro.system.core.common.helper.StringHelper;
import com.adminpro.system.tools.domains.entity.exceptionlog.ExceptionLogEntity;
import com.adminpro.system.tools.domains.entity.exceptionlog.ExceptionLogService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.rendersnake.HtmlAttributesFactory;
import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Created by simon on 2017/6/9.
 */
@RestController
@Tag(name = "错误处理", description = "Spring Boot全局错误处理接口，统一处理系统异常")
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
    @Operation(summary = "全局错误处理", description = "Spring Boot错误控制器，统一处理系统异常并返回JSON格式的错误信息，同时记录异常日志")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - HTTP状态码始终为200，业务状态码在response body中返回
                - restCode=401: 未授权
                - restCode=403: 禁止访问
                - restCode=404: 资源不存在
                - restCode=500: 服务器错误
                """,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = R.class))
    )
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

        logger.info("记录异常日志 - 状态码: {}, 路径: {}, 异常类型: {}, 消息: {}, 堆栈长度: {}", 
                status, path, exception, message, trace != null ? trace.length() : 0);

        ExceptionLogEntity log = new ExceptionLogEntity();

        // Use full stack trace for details
        StringBuilder details = new StringBuilder();
        details.append("=== 异常详情 ===\n");
        details.append("状态码: ").append(status != null ? status : "N/A").append("\n");
        details.append("路径: ").append(path != null ? path : "N/A").append("\n");
        details.append("异常类型: ").append(exception != null ? exception : "N/A").append("\n");
        details.append("消息: ").append(message != null ? message : "N/A").append("\n");
        details.append("\n=== 堆栈跟踪 ===\n");
        if (trace != null && !trace.isEmpty()) {
            details.append(trace);
        } else {
            details.append("无堆栈跟踪信息");
            logger.warn("异常日志缺少堆栈跟踪信息: path={}, exception={}, message={}", path, exception, message);
        }
        
        String detailsStr = details.toString();
        if (detailsStr.length() < 100) {
            logger.warn("异常详情过短，可能丢失信息: length={}, details={}", detailsStr.length(), detailsStr);
        }
        log.setDetails(detailsStr);

        // Use exception class name as type, fallback to message or "Unknown Error"
        if (exception != null && !exception.isEmpty()) {
            log.setType(exception);
        } else if (message != null && !message.isEmpty()) {
            log.setType(StringUtils.abbreviate(message, 255));
        } else {
            log.setType("Unknown Error");
        }

        log.setPath(path != null ? path : "");
        try {
            exceptionLogService.create(log);
            logger.info("异常日志记录成功: id={}, type={}, path={}", log.getId(), log.getType(), log.getPath());
        } catch (Exception e) {
            logger.error("记录异常日志失败: path={}, message={}, exception={}", path, message, exception, e);
        }
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions errorAttributeOptions) {
        WebRequest webRequest = new ServletWebRequest(request);
        return errorAttributes.getErrorAttributes(webRequest, errorAttributeOptions);
    }
}
