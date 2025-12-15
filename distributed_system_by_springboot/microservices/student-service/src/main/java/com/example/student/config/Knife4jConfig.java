package com.example.student.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("学生服务API")
                        .description("学生信息管理服务接口文档")
                        .version("1.0.0"));
    }
}
