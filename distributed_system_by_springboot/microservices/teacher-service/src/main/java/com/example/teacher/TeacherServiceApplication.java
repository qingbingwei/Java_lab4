package com.example.teacher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 教师服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.example.teacher", "com.example.common"})
@MapperScan("com.example.teacher.mapper")
public class TeacherServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeacherServiceApplication.class, args);
    }
}
