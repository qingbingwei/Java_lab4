package com.example.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 认证服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.example.auth", "com.example.common"})
@MapperScan("com.example.auth.mapper")
@EnableFeignClients
@EnableAsync
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
