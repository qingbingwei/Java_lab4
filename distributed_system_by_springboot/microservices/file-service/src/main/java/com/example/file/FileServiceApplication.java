package com.example.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.example.file", "com.example.common"})
@MapperScan("com.example.file.mapper")
public class FileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}
