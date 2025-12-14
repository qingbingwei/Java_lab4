package com.example.studentscore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentscore.common.PageResult;
import com.example.studentscore.common.Result;
import com.example.studentscore.dto.TeachingClassDTO;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.query.TeachingClassQuery;
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
 * 教学班管理控制器
 *
 * @author system
 */
@Tag(name = "教学班管理", description = "教学班信息的增删改查接口")
@RestController
@RequestMapping("/teaching-classes")
@RequiredArgsConstructor
public class TeachingClassController {

    private final TeachingClassService teachingClassService;

    @Operation(summary = "分页查询教学班列表")
    @GetMapping("/page")
    public Result<PageResult<TeachingClassVO>> pageTeachingClasses(TeachingClassQuery query) {
        IPage<TeachingClassVO> page = teachingClassService.pageTeachingClasses(query);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Operation(summary = "获取所有教学班列表")
    @GetMapping("/list")
    public Result<List<TeachingClassVO>> listAllTeachingClasses() {
        return Result.success(teachingClassService.listAllTeachingClasses());
    }

    @Operation(summary = "根据ID获取教学班详情")
    @GetMapping("/{id}")
    public Result<TeachingClassVO> getTeachingClassById(
            @Parameter(description = "教学班ID") @PathVariable Long id) {
        return Result.success(teachingClassService.getTeachingClassDetail(id));
    }

    @Operation(summary = "根据教学班号获取教学班")
    @GetMapping("/classId/{classId}")
    public Result<TeachingClass> getTeachingClassByClassId(
            @Parameter(description = "教学班号") @PathVariable String classId) {
        return Result.success(teachingClassService.getByClassId(classId));
    }

    @Operation(summary = "添加教学班")
    @PostMapping
    public Result<Boolean> addTeachingClass(@Valid @RequestBody TeachingClassDTO dto) {
        return Result.success("添加成功", teachingClassService.addTeachingClass(dto));
    }

    @Operation(summary = "更新教学班信息")
    @PutMapping
    public Result<Boolean> updateTeachingClass(@Valid @RequestBody TeachingClassDTO dto) {
        return Result.success("更新成功", teachingClassService.updateTeachingClass(dto));
    }

    @Operation(summary = "删除教学班")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTeachingClass(
            @Parameter(description = "教学班ID") @PathVariable Long id) {
        return Result.success("删除成功", teachingClassService.removeById(id));
    }

    @Operation(summary = "批量删除教学班")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteTeachingClasses(@RequestBody List<Long> ids) {
        return Result.success("批量删除成功", teachingClassService.removeByIds(ids));
    }

    @Operation(summary = "获取教学班学生列表（带成绩）")
    @GetMapping("/{id}/students")
    public Result<List<Object>> getClassStudents(
            @Parameter(description = "教学班ID") @PathVariable Long id) {
        return Result.success(teachingClassService.getClassStudentsWithScores(id));
    }

    @Operation(summary = "根据教师ID获取其教学班列表")
    @GetMapping("/teacher/{teacherDbId}")
    public Result<List<TeachingClassVO>> getTeachingClassesByTeacher(
            @Parameter(description = "教师数据库ID") @PathVariable Long teacherDbId) {
        return Result.success(teachingClassService.getTeacherClasses(teacherDbId));
    }
}
