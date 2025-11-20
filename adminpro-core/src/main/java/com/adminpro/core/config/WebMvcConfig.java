package com.adminpro.core.config;

import com.adminpro.core.base.util.FileUtil;
import com.adminpro.core.base.util.JsonUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
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

    /**
     * 访问图片方法
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
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
            imagesPath = imagesPath.substring(0, imagesPath.lastIndexOf("/")) + "/upload/";
            publicFileDir = imagesPath;
        }
        registry.addResourceHandler("/upload/**").addResourceLocations(publicFileDir);
        // Spring Boot 3.x 使用 springdoc-openapi，swagger-ui 路径为 /swagger-ui.html
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
                String path = (String) errorAttributes.get("path");
                String trace = (String) errorAttributes.get("trace");
                String message = (String) errorAttributes.get(MESSAGE);
                apiResponse.put(MESSAGE, message != null ? message : "Unknown error");
                apiResponse.put("success", false);
                apiResponse.put("path", path != null ? path : "");
                apiResponse.put("errors", null);
                apiResponse.put("data", null);
                logger.debug("===============errorAttributes start===========================");
                String json = JsonUtil.toJson(errorAttributes);
                logger.debug("error, {}", json);
                logger.debug("===============errorAttributes end===========================");
                return apiResponse;
            }

        };
    }
}
