package com.example.studentscore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentscore.common.PageResult;
import com.example.studentscore.common.Result;
import com.example.studentscore.dto.CourseDTO;
import com.example.studentscore.entity.Course;
import com.example.studentscore.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 *
 * @author system
 */
@Tag(name = "课程管理", description = "课程信息的增删改查接口")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "分页查询课程列表")
    @GetMapping("/page")
    public Result<PageResult<Course>> pageCourses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        IPage<Course> page = courseService.pageCourses(pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Operation(summary = "获取所有课程列表")
    @GetMapping("/list")
    public Result<List<Course>> listAllCourses() {
        return Result.success(courseService.list());
    }

    @Operation(summary = "根据ID获取课程详情")
    @GetMapping("/{id}")
    public Result<Course> getCourseById(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }

    @Operation(summary = "根据课程编号获取课程")
    @GetMapping("/courseId/{courseId}")
    public Result<Course> getCourseByCourseId(
            @Parameter(description = "课程编号") @PathVariable String courseId) {
        return Result.success(courseService.getByCourseId(courseId));
    }

    @Operation(summary = "添加课程")
    @PostMapping
    public Result<Boolean> addCourse(@Valid @RequestBody CourseDTO dto) {
        return Result.success("添加成功", courseService.addCourse(dto));
    }

    @Operation(summary = "更新课程信息")
    @PutMapping
    public Result<Boolean> updateCourse(@Valid @RequestBody CourseDTO dto) {
        return Result.success("更新成功", courseService.updateCourse(dto));
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCourse(
            @Parameter(description = "课程ID") @PathVariable Long id) {
        return Result.success("删除成功", courseService.removeById(id));
    }

    @Operation(summary = "批量删除课程")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteCourses(@RequestBody List<Long> ids) {
        return Result.success("批量删除成功", courseService.removeByIds(ids));
    }

    @Operation(summary = "批量添加课程")
    @PostMapping("/batch")
    public Result<Boolean> batchAddCourses(@Valid @RequestBody List<CourseDTO> dtoList) {
        return Result.success("批量添加成功", courseService.batchAddCourses(dtoList));
    }
}
