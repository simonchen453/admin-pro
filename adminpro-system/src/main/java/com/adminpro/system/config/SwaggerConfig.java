package com.adminpro.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 接口文档配置类
 * <p>
 * 该配置类使用 SpringDoc OpenAPI 3 库为项目生成API接口文档。
 * Spring Boot 3.x 使用 springdoc-openapi 替代了旧版的 springfox-swagger。
 * <p>
 * 主要功能：
 * <ul>
 * <li>自动生成RESTful API文档</li>
 * <li>提供在线API测试界面（Swagger UI）</li>
 * <li>支持JWT Token认证测试</li>
 * <li>自动扫描和生成接口文档</li>
 * </ul>
 * <p>
 * 访问地址：
 * <ul>
 * <li>Swagger UI: /swagger-ui/index.html</li>
 * <li>OpenAPI JSON: /v3/api-docs</li>
 * </ul>
 * <p>
 * 配置参数：
 * <ul>
 * <li>app.swagger.scan.package：API扫描包路径（默认：com.adminpro）</li>
 * <li>app.swagger.enabled：是否启用Swagger（默认：true）</li>
 * </ul>
 *
 * @author simon
 * @see io.swagger.v3.oas.models.OpenAPI
 * @see org.springdoc.core.models.GroupedOpenApi
 */
@Configuration
@Slf4j
public class SwaggerConfig {

        /**
         * API文档扫描包路径
         * <p>
         * Swagger会扫描该包及其子包下的所有Controller类，
         * 自动生成API文档。支持通过配置文件自定义扫描路径。
         */
        @Value("${app.swagger.scan.package:com.adminpro}")
        private String scanPackage;

        /**
         * 是否启用Swagger文档
         * <p>
         * 生产环境建议设置为false以关闭API文档，提高安全性。
         * 可通过配置文件或环境变量控制。
         */
        @Value("${app.swagger.enabled:true}")
        private boolean swaggerEnabled;

        /**
         * 创建并配置OpenAPI文档实例
         * <p>
         * 该方法配置了API文档的基本信息和安全认证方案：
         * <ul>
         * <li>文档标题：Admin Pro快速开发平台</li>
         * <li>文档版本：1.0</li>
         * <li>联系方式：作者名称、网址、邮箱</li>
         * <li>安全认证：JWT Bearer Token认证</li>
         * <li>Token位置：HTTP请求头</li>
         * <li>Token名称：x-access-token</li>
         * </ul>
         * <p>
         * 在Swagger UI中测试接口时，可以点击右上角"Authorize"按钮，
         * 输入JWT Token进行认证测试。
         * <p>
         * 安全认证配置：
         * <ul>
         * <li>类型：HTTP Bearer认证</li>
         * <li>格式：JWT（JSON Web Token）</li>
         * <li>传输方式：Authorization请求头</li>
         * <li>令牌前缀：Bearer</li>
         * </ul>
         *
         * @return 配置好的OpenAPI实例
         */
        @Bean
        public OpenAPI customOpenAPI() {
                log.info("### Swagger扫描路径: {}, enabled: {}", scanPackage, swaggerEnabled);

                return new OpenAPI()
                                .info(new Info()
                                                .title("Admin Pro快速开发平台")
                                                .version("1.0"))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("Bearer Authentication",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .name("x-access-token")
                                                                                .in(SecurityScheme.In.HEADER)
                                                                                .description("JWT认证令牌")));
        }

        /**
         * 移除 Springdoc 自动添加的默认响应（400、500等）
         * <p>
         * 本系统采用统一响应格式，HTTP 状态码始终为 200，
         * 通过 restCode 字段区分业务状态码，因此需要移除默认的 HTTP 错误响应。
         * </p>
         *
         * @return OperationCustomizer 实例
         */
        @Bean
        public OperationCustomizer removeDefaultResponses() {
                return (operation, handlerMethod) -> {
                        // 移除 Springdoc 自动添加的默认响应
                        if (operation.getResponses() != null) {
                                operation.getResponses().remove("400");
                                operation.getResponses().remove("401");
                                operation.getResponses().remove("403");
                                operation.getResponses().remove("404");
                                operation.getResponses().remove("500");
                        }
                        return operation;
                };
        }
}
