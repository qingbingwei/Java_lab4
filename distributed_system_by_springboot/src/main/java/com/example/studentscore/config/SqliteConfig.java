package com.example.studentscore.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * SQLite数据库配置 - 启动时创建数据目录
 *
 * @author system
 */
@Configuration
public class SqliteConfig {

    @PostConstruct
    public void init() {
        // 创建数据目录
        File dataDir = new File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("创建数据目录: " + dataDir.getAbsolutePath());
        }
    }
}
