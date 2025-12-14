package com.example.studentscore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentscore.common.PageResult;
import com.example.studentscore.common.Result;
import com.example.studentscore.dto.EnrollmentDTO;
import com.example.studentscore.entity.Enrollment;
import com.example.studentscore.service.EnrollmentService;
import com.example.studentscore.vo.EnrollmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 选课管理控制器
 *
 * @author system
 */
@Tag(name = "选课管理", description = "学生选课、退课等接口")
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "分页查询选课记录")
    @GetMapping("/page")
    public Result<PageResult<Enrollment>> pageEnrollments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentDbId,
            @Parameter(description = "教学班ID") @RequestParam(required = false) Long teachingClassDbId) {
        IPage<Enrollment> page = enrollmentService.pageEnrollments(pageNum, pageSize, studentDbId, teachingClassDbId);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Operation(summary = "获取学生已选课程")
    @GetMapping("/student/{studentDbId}")
    public Result<List<EnrollmentVO>> getStudentEnrollments(
            @Parameter(description = "学生数据库ID") @PathVariable Long studentDbId) {
        return Result.success(enrollmentService.getStudentEnrollments(studentDbId));
    }

    @Operation(summary = "获取教学班学生列表")
    @GetMapping("/class/{teachingClassDbId}")
    public Result<List<EnrollmentVO>> getClassEnrollments(
            @Parameter(description = "教学班数据库ID") @PathVariable Long teachingClassDbId) {
        return Result.success(enrollmentService.getClassEnrollments(teachingClassDbId));
    }

    @Operation(summary = "检查是否已选课")
    @GetMapping("/check")
    public Result<Boolean> checkEnrolled(
            @Parameter(description = "学生数据库ID") @RequestParam Long studentDbId,
            @Parameter(description = "教学班数据库ID") @RequestParam Long teachingClassDbId) {
        return Result.success(enrollmentService.isEnrolled(studentDbId, teachingClassDbId));
    }

    @Operation(summary = "学生选课")
    @PostMapping
    public Result<Boolean> enroll(@Valid @RequestBody EnrollmentDTO dto) {
        return Result.success("选课成功", enrollmentService.enroll(dto));
    }

    @Operation(summary = "学生退课")
    @DeleteMapping
    public Result<Boolean> drop(
            @Parameter(description = "学生数据库ID") @RequestParam Long studentDbId,
            @Parameter(description = "教学班数据库ID") @RequestParam Long teachingClassDbId) {
        return Result.success("退课成功", enrollmentService.drop(studentDbId, teachingClassDbId));
    }

    @Operation(summary = "批量选课")
    @PostMapping("/batch")
    public Result<Boolean> batchEnroll(@Valid @RequestBody List<EnrollmentDTO> dtoList) {
        return Result.success("批量选课成功", enrollmentService.batchEnroll(dtoList));
    }
}
