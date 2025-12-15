package com.example.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.CourseDTO;
import com.example.common.entity.Course;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@Tag(name = "课程管理", description = "课程信息管理相关接口")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    
    @Operation(summary = "分页查询课程")
    @GetMapping("/page")
    public Result<PageResult<Course>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "课程号") @RequestParam(required = false) String courseId,
            @Parameter(description = "课程名") @RequestParam(required = false) String name,
            @Parameter(description = "课程类型") @RequestParam(required = false) String type) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        IPage<Course> result = courseService.page(page, courseId, name, type);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }
    
    @Operation(summary = "获取所有课程")
    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.success(courseService.list());
    }
    
    @Operation(summary = "根据ID获取课程")
    @GetMapping("/{id}")
    public Result<Course> getById(@Parameter(description = "课程ID") @PathVariable("id") String id) {
        Course course = courseService.getById(id);
        return course != null ? Result.success(course) : Result.error("课程不存在");
    }
    
    @Operation(summary = "根据课程号获取课程")
    @GetMapping("/courseId/{courseId}")
    public Result<Course> getByCourseId(@Parameter(description = "课程号") @PathVariable("courseId") String courseId) {
        Course course = courseService.getByCourseId(courseId);
        return course != null ? Result.success(course) : Result.error("课程不存在");
    }
    
    @Operation(summary = "新增课程")
    @PostMapping
    public Result<Boolean> save(@RequestBody CourseDTO dto) {
        return Result.success(courseService.save(dto));
    }
    
    @Operation(summary = "更新课程")
    @PutMapping
    public Result<Boolean> update(@RequestBody CourseDTO dto) {
        return Result.success(courseService.update(dto));
    }
    
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "课程ID") @PathVariable("id") String id) {
        return Result.success(courseService.delete(id));
    }
    
    @Operation(summary = "批量删除课程")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(courseService.deleteBatch(ids));
    }
    
    @Operation(summary = "检查课程号是否存在")
    @GetMapping("/exists/{courseId}")
    public Result<Boolean> existsByCourseId(@Parameter(description = "课程号") @PathVariable("courseId") String courseId) {
        return Result.success(courseService.existsByCourseId(courseId));
    }
    
    @Operation(summary = "统计课程总数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(courseService.count());
    }
}
