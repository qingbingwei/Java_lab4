package com.example.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.EnrollmentDTO;
import com.example.common.entity.Enrollment;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.common.vo.EnrollmentVO;
import com.example.course.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 选课控制器
 */
@Tag(name = "选课管理", description = "选课信息管理相关接口")
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    @Operation(summary = "分页查询选课")
    @GetMapping("/page")
    public Result<PageResult<EnrollmentVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学号") @RequestParam(required = false) String studentId,
            @Parameter(description = "教学班编号") @RequestParam(required = false) String classId) {
        Page<Enrollment> page = new Page<>(pageNum, pageSize);
        IPage<EnrollmentVO> result = enrollmentService.page(page, studentId, classId);
        return Result.success(PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize()));
    }
    
    @Operation(summary = "根据学号获取选课列表")
    @GetMapping("/student/{studentId}")
    public Result<List<EnrollmentVO>> getByStudentId(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        return Result.success(enrollmentService.getByStudentId(studentId));
    }
    
    @Operation(summary = "根据教学班编号获取选课列表")
    @GetMapping("/class/{classId}")
    public Result<List<EnrollmentVO>> getByClassId(@Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        return Result.success(enrollmentService.getByClassId(classId));
    }
    
    @Operation(summary = "根据ID获取选课")
    @GetMapping("/{id}")
    public Result<EnrollmentVO> getById(@Parameter(description = "选课ID") @PathVariable("id") String id) {
        EnrollmentVO vo = enrollmentService.getById(id);
        return vo != null ? Result.success(vo) : Result.error("选课记录不存在");
    }
    
    @Operation(summary = "新增选课")
    @PostMapping
    public Result<Boolean> save(@RequestBody EnrollmentDTO dto) {
        return Result.success(enrollmentService.save(dto));
    }
    
    @Operation(summary = "批量选课")
    @PostMapping("/batch")
    public Result<Boolean> saveBatch(@RequestBody List<EnrollmentDTO> dtos) {
        return Result.success(enrollmentService.saveBatch(dtos));
    }
    
    @Operation(summary = "删除选课")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "选课ID") @PathVariable("id") String id) {
        return Result.success(enrollmentService.delete(id));
    }
    
    @Operation(summary = "取消选课")
    @DeleteMapping("/cancel")
    public Result<Boolean> cancel(
            @Parameter(description = "学号") @RequestParam("studentId") String studentId,
            @Parameter(description = "教学班编号") @RequestParam("classId") String classId) {
        return Result.success(enrollmentService.cancel(studentId, classId));
    }
    
    @Operation(summary = "批量删除选课")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(enrollmentService.deleteBatch(ids));
    }
    
    @Operation(summary = "检查是否已选课")
    @GetMapping("/exists")
    public Result<Boolean> exists(
            @Parameter(description = "学号") @RequestParam("studentId") String studentId,
            @Parameter(description = "教学班编号") @RequestParam("classId") String classId) {
        return Result.success(enrollmentService.exists(studentId, classId));
    }
    
    @Operation(summary = "统计选课总数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(enrollmentService.count());
    }
    
    @Operation(summary = "统计教学班选课人数")
    @GetMapping("/count/{classId}")
    public Result<Long> countByClassId(@Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        return Result.success(enrollmentService.countByClassId(classId));
    }
}
