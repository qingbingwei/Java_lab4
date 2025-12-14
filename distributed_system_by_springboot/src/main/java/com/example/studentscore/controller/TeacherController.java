package com.example.studentscore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentscore.common.PageResult;
import com.example.studentscore.common.Result;
import com.example.studentscore.dto.TeacherDTO;
import com.example.studentscore.entity.Teacher;
import com.example.studentscore.service.TeacherService;
import com.example.studentscore.service.TeachingClassService;
import com.example.studentscore.vo.TeachingClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理控制器
 *
 * @author system
 */
@Tag(name = "教师管理", description = "教师信息的增删改查接口")
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final TeachingClassService teachingClassService;

    @Operation(summary = "分页查询教师列表")
    @GetMapping("/page")
    public Result<PageResult<Teacher>> pageTeachers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        IPage<Teacher> page = teacherService.pageTeachers(pageNum, pageSize, keyword);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Operation(summary = "获取所有教师列表")
    @GetMapping("/list")
    public Result<List<Teacher>> listAllTeachers() {
        return Result.success(teacherService.list());
    }

    @Operation(summary = "根据ID获取教师详情")
    @GetMapping("/{id}")
    public Result<Teacher> getTeacherById(
            @Parameter(description = "教师ID") @PathVariable Long id) {
        return Result.success(teacherService.getById(id));
    }

    @Operation(summary = "根据教师编号获取教师")
    @GetMapping("/teacherId/{teacherId}")
    public Result<Teacher> getTeacherByTeacherId(
            @Parameter(description = "教师编号") @PathVariable String teacherId) {
        return Result.success(teacherService.getByTeacherId(teacherId));
    }

    @Operation(summary = "获取教师的教学班列表")
    @GetMapping("/{id}/classes")
    public Result<List<TeachingClassVO>> getTeacherClasses(
            @Parameter(description = "教师ID") @PathVariable Long id) {
        return Result.success(teachingClassService.getTeacherClasses(id));
    }

    @Operation(summary = "添加教师")
    @PostMapping
    public Result<Boolean> addTeacher(@Valid @RequestBody TeacherDTO dto) {
        return Result.success("添加成功", teacherService.addTeacher(dto));
    }

    @Operation(summary = "更新教师信息")
    @PutMapping
    public Result<Boolean> updateTeacher(@Valid @RequestBody TeacherDTO dto) {
        return Result.success("更新成功", teacherService.updateTeacher(dto));
    }

    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeacher(
            @Parameter(description = "教师ID") @PathVariable Long id) {
        return Result.success("删除成功", teacherService.removeById(id));
    }

    @Operation(summary = "批量删除教师")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteTeachers(@RequestBody List<Long> ids) {
        return Result.success("批量删除成功", teacherService.removeByIds(ids));
    }

    @Operation(summary = "批量添加教师")
    @PostMapping("/batch")
    public Result<Boolean> batchAddTeachers(@Valid @RequestBody List<TeacherDTO> dtoList) {
        return Result.success("批量添加成功", teacherService.batchAddTeachers(dtoList));
    }
}
