package com.example.score;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 成绩服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.example.score", "com.example.common"})
@MapperScan("com.example.score.mapper")
public class ScoreServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScoreServiceApplication.class, args);
    }
}
