package com.example.auth.config;

import com.example.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化配置
 * 在应用启动完成后自动初始化默认用户
 */
@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class DatabaseInitConfig {

    private final AuthService authService;

    /**
     * 应用启动完成后延迟初始化用户数据
     * 延迟执行是为了等待其他服务（student-service, teacher-service）启动完成
     */
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("应用启动完成，准备初始化用户数据...");
        
        // 延迟5秒，等待其他服务启动
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        
        try {
            authService.initDefaultUsers();
        } catch (Exception e) {
            log.error("初始化用户数据失败: {}", e.getMessage());
        }
    }
}
