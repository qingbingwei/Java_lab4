package com.example.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.dto.TeachingClassDTO;
import com.example.common.query.TeachingClassQuery;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.common.vo.ClassStudentVO;
import com.example.common.vo.TeachingClassVO;
import com.example.course.service.TeachingClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教学班控制器
 */
@Tag(name = "教学班管理", description = "教学班信息管理相关接口")
@RestController
@RequestMapping("/teaching-classes")
@RequiredArgsConstructor
public class TeachingClassController {
    
    private final TeachingClassService teachingClassService;
    
    @Operation(summary = "分页查询教学班")
    @GetMapping("/page")
    public Result<PageResult<TeachingClassVO>> page(TeachingClassQuery query) {
        IPage<TeachingClassVO> page = teachingClassService.page(query);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
    
    @Operation(summary = "获取所有教学班")
    @GetMapping("/list")
    public Result<List<TeachingClassVO>> list() {
        return Result.success(teachingClassService.list());
    }
    
    @Operation(summary = "根据ID获取教学班")
    @GetMapping("/{id}")
    public Result<TeachingClassVO> getById(@Parameter(description = "教学班ID") @PathVariable("id") String id) {
        TeachingClassVO vo = teachingClassService.getById(id);
        return vo != null ? Result.success(vo) : Result.error("教学班不存在");
    }
    
    @Operation(summary = "获取教学班学生列表（含成绩）")
    @GetMapping("/{id}/students")
    public Result<List<ClassStudentVO>> getStudents(@Parameter(description = "教学班数据库ID") @PathVariable("id") Long id) {
        return Result.success(teachingClassService.getStudents(id));
    }
    
    @Operation(summary = "根据教学班编号获取教学班")
    @GetMapping("/classId/{classId}")
    public Result<TeachingClassVO> getByClassId(@Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        TeachingClassVO vo = teachingClassService.getByClassId(classId);
        return vo != null ? Result.success(vo) : Result.error("教学班不存在");
    }
    
    @Operation(summary = "根据教师工号获取教学班")
    @GetMapping("/teacher/{teacherId}")
    public Result<List<TeachingClassVO>> getByTeacherId(@Parameter(description = "教师工号") @PathVariable("teacherId") String teacherId) {
        return Result.success(teachingClassService.getByTeacherId(teacherId));
    }
    
    @Operation(summary = "新增教学班")
    @PostMapping
    public Result<Boolean> save(@RequestBody TeachingClassDTO dto) {
        return Result.success(teachingClassService.save(dto));
    }
    
    @Operation(summary = "更新教学班")
    @PutMapping
    public Result<Boolean> update(@RequestBody TeachingClassDTO dto) {
        return Result.success(teachingClassService.update(dto));
    }
    
    @Operation(summary = "删除教学班")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "教学班ID") @PathVariable("id") String id) {
        return Result.success(teachingClassService.delete(id));
    }
    
    @Operation(summary = "批量删除教学班")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(teachingClassService.deleteBatch(ids));
    }
    
    @Operation(summary = "检查教学班编号是否存在")
    @GetMapping("/exists/{classId}")
    public Result<Boolean> existsByClassId(@Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        return Result.success(teachingClassService.existsByClassId(classId));
    }
    
    @Operation(summary = "统计教学班总数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(teachingClassService.count());
    }
}
