package com.adminpro.framework.config;

import com.adminpro.framework.base.util.FileUtil;
import com.adminpro.framework.base.util.JsonUtil;
import com.adminpro.framework.client.interceptor.ClientInfoInterceptor;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * @author simon
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    public static final String INVALID_AUTH_TOKEN_EXCEPTION = "Invalid Auth Token";
    public static final String NOT_ACCEPTABLE_EXCEPTION = "Access Denied";
    public static final String REST_CODE = "restCode";
    public static final String MESSAGE = "message";

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClientInfoInterceptor clientInfoInterceptor;

    /**
     * 访问图片方法
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String publicFileDir = FileUtil.PUBLIC_FILE_DIR;
        String privateFileDir = FileUtil.PRIVATE_FILE_DIR;
        logger.info("文件上传路径, public：{}", publicFileDir);
        logger.info("文件上传路径, private：{}", privateFileDir);
        if ("".equals(publicFileDir) || "${app.upload.dir}".equals(publicFileDir)) {
            String imagesPath = WebMvcConfig.class.getClassLoader().getResource("").getPath();
            if (imagesPath.indexOf(".jar") > -1) {
                imagesPath = imagesPath.substring(0, imagesPath.indexOf(".jar"));
            } else if (imagesPath.indexOf("classes") > -1) {
                imagesPath = "file:" + imagesPath.substring(0, imagesPath.indexOf("classes"));
            }
            imagesPath = imagesPath.substring(0, imagesPath.lastIndexOf("/")) + FileUtil.FILE_URL_PREFIX + "/";
            publicFileDir = imagesPath;
        }
        registry.addResourceHandler(FileUtil.FILE_URL_PREFIX + "/**").addResourceLocations(publicFileDir);
        // Spring Boot 3.x 使用 springdoc-openapi，swagger-ui 路径为 /swagger-ui.html
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        if (clientInfoInterceptor != null) {
            registry.addInterceptor(clientInfoInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns("/js/**", "/plugins/**", "/css/**", "/images/**", "/img/**", "/icons/**");
        }
    }

    /**
     * 设置默认错误返回
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, options);

                Map<String, Object> apiResponse = new HashedMap();
                String error = (String) errorAttributes.get(MESSAGE);
                if (StringUtils.equals(error, INVALID_AUTH_TOKEN_EXCEPTION)) {
                    apiResponse.put(REST_CODE, String.valueOf(HttpStatus.UNAUTHORIZED.value()));
                } else if (StringUtils.equals(error, NOT_ACCEPTABLE_EXCEPTION)) {
                    apiResponse.put(REST_CODE, String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()));
                } else {
                    Object status = errorAttributes.get("status");
                    if (status != null) {
                        apiResponse.put(REST_CODE, String.valueOf(status));
                    } else {
                        apiResponse.put(REST_CODE, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
                    }
                }

                // Explicitly try to get the exception
                Throwable t = getError(requestAttributes);

                String path = (String) errorAttributes.get("path");
                String trace = (String) errorAttributes.get("trace");
                String message = (String) errorAttributes.get(MESSAGE);
                String exception = (String) errorAttributes.get("exception");

                // If super failed to get details but we successfully got the exception,
                // populate them manually
                if (t != null) {
                    logger.error("捕获到异常: {}", t.getClass().getName(), t);
                    
                    if (message == null || message.isEmpty()) {
                        message = t.getMessage();
                        if (message == null || message.isEmpty()) {
                            message = t.getClass().getSimpleName();
                        }
                    }
                    if (exception == null) {
                        exception = t.getClass().getName();
                    }
                    if (trace == null) {
                        try (java.io.StringWriter sw = new java.io.StringWriter();
                                java.io.PrintWriter pw = new java.io.PrintWriter(sw)) {
                            t.printStackTrace(pw);
                            trace = sw.toString();
                        } catch (Exception ignore) {
                            logger.error("生成堆栈跟踪失败", ignore);
                        }
                    }
                } else {
                    // unexpected error, check status code
                    Object statusCode = requestAttributes.getAttribute("jakarta.servlet.error.status_code",
                            WebRequest.SCOPE_REQUEST);
                    if (statusCode != null) {
                        apiResponse.put(REST_CODE, statusCode.toString());
                    }
                    Object errorMessage = requestAttributes.getAttribute("jakarta.servlet.error.message",
                            WebRequest.SCOPE_REQUEST);
                    if (errorMessage != null) {
                        message = errorMessage.toString();
                    }
                    
                    Object errorException = requestAttributes.getAttribute("jakarta.servlet.error.exception",
                            WebRequest.SCOPE_REQUEST);
                    if (errorException instanceof Throwable) {
                        Throwable ex = (Throwable) errorException;
                        logger.error("从servlet属性中获取到异常: {}", ex.getClass().getName(), ex);
                        exception = ex.getClass().getName();
                        if (message == null || message.isEmpty()) {
                            message = ex.getMessage();
                        }
                        try (java.io.StringWriter sw = new java.io.StringWriter();
                                java.io.PrintWriter pw = new java.io.PrintWriter(sw)) {
                            ex.printStackTrace(pw);
                            trace = sw.toString();
                        } catch (Exception ignore) {
                            logger.error("生成堆栈跟踪失败", ignore);
                        }
                    }
                }

                apiResponse.put(MESSAGE, message != null ? message : "Unknown error");
                apiResponse.put("success", false);
                apiResponse.put("path", path != null ? path : "");
                // Pass trace and exception to controller for logging, but controller must
                // remove them before responding
                if (trace != null) {
                    apiResponse.put("trace", trace);
                }
                if (exception != null) {
                    apiResponse.put("exception", exception);
                }

                apiResponse.put("errors", null);
                apiResponse.put("data", null);
                logger.debug("===============errorAttributes start===========================");
                String json = JsonUtil.toJson(errorAttributes);
                logger.debug("原始错误属性: {}", json);
                logger.debug("异常类型: {}, 消息: {}, 堆栈长度: {}", exception, message, trace != null ? trace.length() : 0);
                logger.debug("===============errorAttributes end===========================");
                return apiResponse;
            }

        };
    }
}
