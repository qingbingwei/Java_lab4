package com.example.auth.feign;

import com.example.common.entity.Student;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 学生服务Feign客户端
 */
@FeignClient(name = "student-service", url = "${service.student-url:http://localhost:8082}")
public interface StudentFeignClient {

    /**
     * 根据ID获取学生
     */
    @GetMapping("/students/{id}")
    Result<Student> getById(@PathVariable("id") Long id);

    /**
     * 获取所有学生列表
     */
    @GetMapping("/students/list")
    Result<List<Student>> getList();
}
