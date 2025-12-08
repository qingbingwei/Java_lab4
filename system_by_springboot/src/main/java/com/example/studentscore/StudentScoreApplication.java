package com.example.studentscore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生成绩管理系统启动类
 *
 * @author system
 */
@SpringBootApplication
@MapperScan("com.example.studentscore.mapper")
public class StudentScoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentScoreApplication.class, args);
        System.out.println("========================================");
        System.out.println("  学生成绩管理系统启动成功!");
        System.out.println("  接口文档: http://localhost:8080/api/doc.html");
        System.out.println("  数据库: SQLite (自动定位项目目录)");
        System.out.println("  默认账号: admin / 123456");
        System.out.println("========================================");
    }
}
