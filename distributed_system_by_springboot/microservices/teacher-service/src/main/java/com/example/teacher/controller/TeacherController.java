package com.example.teacher.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.TeacherDTO;
import com.example.common.entity.Teacher;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.teacher.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师控制器
 */
@Tag(name = "教师管理", description = "教师信息管理相关接口")
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    
    @Operation(summary = "分页查询教师")
    @GetMapping("/page")
    public Result<PageResult<Teacher>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "工号") @RequestParam(required = false) String teacherId,
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "部门") @RequestParam(required = false) String department) {
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        IPage<Teacher> result = teacherService.page(page, teacherId, name, department);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }
    
    @Operation(summary = "获取所有教师")
    @GetMapping("/list")
    public Result<List<Teacher>> list() {
        return Result.success(teacherService.list());
    }
    
    @Operation(summary = "根据ID获取教师")
    @GetMapping("/{id}")
    public Result<Teacher> getById(@Parameter(description = "教师ID") @PathVariable("id") String id) {
        Teacher teacher = teacherService.getById(id);
        return teacher != null ? Result.success(teacher) : Result.error("教师不存在");
    }
    
    @Operation(summary = "根据工号获取教师")
    @GetMapping("/teacherId/{teacherId}")
    public Result<Teacher> getByTeacherId(@Parameter(description = "工号") @PathVariable("teacherId") String teacherId) {
        Teacher teacher = teacherService.getByTeacherId(teacherId);
        return teacher != null ? Result.success(teacher) : Result.error("教师不存在");
    }
    
    @Operation(summary = "新增教师")
    @PostMapping
    public Result<Boolean> save(@RequestBody TeacherDTO dto) {
        return Result.success(teacherService.save(dto));
    }
    
    @Operation(summary = "更新教师")
    @PutMapping
    public Result<Boolean> update(@RequestBody TeacherDTO dto) {
        return Result.success(teacherService.update(dto));
    }
    
    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "教师ID") @PathVariable("id") String id) {
        return Result.success(teacherService.delete(id));
    }
    
    @Operation(summary = "批量删除教师")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(teacherService.deleteBatch(ids));
    }
    
    @Operation(summary = "检查工号是否存在")
    @GetMapping("/exists/{teacherId}")
    public Result<Boolean> existsByTeacherId(@Parameter(description = "工号") @PathVariable("teacherId") String teacherId) {
        return Result.success(teacherService.existsByTeacherId(teacherId));
    }
    
    @Operation(summary = "统计教师总数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(teacherService.count());
    }
}
