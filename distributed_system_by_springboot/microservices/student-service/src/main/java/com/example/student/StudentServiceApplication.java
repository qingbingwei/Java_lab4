package com.example.student;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 学生服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.example.student", "com.example.common"})
@MapperScan("com.example.student.mapper")
@EnableFeignClients
public class StudentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
