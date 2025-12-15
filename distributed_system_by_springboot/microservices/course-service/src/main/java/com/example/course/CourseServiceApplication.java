package com.example.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 课程服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.example.course", "com.example.common"})
@MapperScan("com.example.course.mapper")
public class CourseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}
