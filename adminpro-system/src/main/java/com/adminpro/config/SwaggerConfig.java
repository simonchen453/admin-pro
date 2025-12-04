package com.adminpro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置
 * Spring Boot 3.x 使用 springdoc-openapi
 *
 * @author simon
 */
@Configuration
@Slf4j
public class SwaggerConfig {

    @Value("${app.swagger.scan.package:com.adminpro}")
    private String scanPackage;

    @Value("${app.swagger.enabled:true}")
    private boolean swaggerEnabled;

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("### Swagger扫描路径: {}, enabled: {}", scanPackage, swaggerEnabled);
        
        return new OpenAPI()
                .info(new Info()
                        .title("Admin Pro快速开发平台")
                        .description("如有疑问，请联系我们")
                        .version("1.0")
                        .contact(new Contact()
                                .name("simon")
                                .url("https://www.adminpro.com/")
                                .email("438767782@qq.com")))
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
}
