package com.example.studentscore.config;

import com.example.studentscore.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库初始化配置
 * 负责在应用启动后初始化默认用户
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig implements ApplicationRunner {

    private final AuthService authService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========================================");
        log.info("开始初始化默认用户数据...");

        try {
            authService.initDefaultUsers();
            log.info("默认用户初始化完成");
        } catch (Exception e) {
            log.error("初始化默认用户失败", e);
        }

        log.info("========================================");
    }
}
