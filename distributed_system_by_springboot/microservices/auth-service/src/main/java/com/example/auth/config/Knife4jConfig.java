package com.example.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("认证服务API")
                        .version("1.0.0")
                        .description("用户认证与授权服务API文档")
                        .contact(new Contact()
                                .name("System")
                                .email("admin@example.com")));
    }
}
