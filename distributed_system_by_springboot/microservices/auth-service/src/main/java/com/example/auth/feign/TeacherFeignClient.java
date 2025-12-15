package com.example.auth.feign;

import com.example.common.entity.Teacher;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 教师服务Feign客户端
 */
@FeignClient(name = "teacher-service", url = "${service.teacher-url:http://localhost:8083}")
public interface TeacherFeignClient {

    /**
     * 根据ID获取教师
     */
    @GetMapping("/teachers/{id}")
    Result<Teacher> getById(@PathVariable("id") Long id);

    /**
     * 获取所有教师列表
     */
    @GetMapping("/teachers/list")
    Result<List<Teacher>> getList();
}
