package com.example.studentscore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentscore.common.PageResult;
import com.example.studentscore.common.Result;
import com.example.studentscore.dto.StudentDTO;
import com.example.studentscore.entity.Student;
import com.example.studentscore.query.StudentQuery;
import com.example.studentscore.service.StudentService;
import com.example.studentscore.vo.StudentScoreDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理控制器
 *
 * @author system
 */
@Tag(name = "学生管理", description = "学生信息的增删改查接口")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "分页查询学生列表")
    @GetMapping("/page")
    public Result<PageResult<Student>> pageStudents(StudentQuery query) {
        IPage<Student> page = studentService.pageStudents(query);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Operation(summary = "获取所有学生列表")
    @GetMapping("/list")
    public Result<List<Student>> listAllStudents() {
        return Result.success(studentService.list());
    }

    @Operation(summary = "根据ID获取学生详情")
    @GetMapping("/{id}")
    public Result<Student> getStudentById(
            @Parameter(description = "学生ID") @PathVariable Long id) {
        return Result.success(studentService.getById(id));
    }

    @Operation(summary = "根据学号获取学生")
    @GetMapping("/studentId/{studentId}")
    public Result<Student> getStudentByStudentId(
            @Parameter(description = "学号") @PathVariable String studentId) {
        return Result.success(studentService.getByStudentId(studentId));
    }

    @Operation(summary = "获取学生成绩详情")
    @GetMapping("/{id}/scores")
    public Result<StudentScoreDetailVO> getStudentScoreDetail(
            @Parameter(description = "学生ID") @PathVariable Long id) {
        return Result.success(studentService.getStudentScoreDetail(id));
    }

    @Operation(summary = "根据学号获取学生成绩详情")
    @GetMapping("/studentId/{studentId}/scores")
    public Result<StudentScoreDetailVO> getStudentScoreDetailByStudentId(
            @Parameter(description = "学号") @PathVariable String studentId) {
        return Result.success(studentService.getStudentScoreDetailByStudentId(studentId));
    }

    @Operation(summary = "添加学生")
    @PostMapping
    public Result<Boolean> addStudent(@Valid @RequestBody StudentDTO dto) {
        return Result.success("添加成功", studentService.addStudent(dto));
    }

    @Operation(summary = "更新学生信息")
    @PutMapping
    public Result<Boolean> updateStudent(@Valid @RequestBody StudentDTO dto) {
        return Result.success("更新成功", studentService.updateStudent(dto));
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteStudent(
            @Parameter(description = "学生ID") @PathVariable Long id) {
        return Result.success("删除成功", studentService.removeById(id));
    }

    @Operation(summary = "批量删除学生")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteStudents(@RequestBody List<Long> ids) {
        return Result.success("批量删除成功", studentService.removeByIds(ids));
    }

    @Operation(summary = "批量添加学生")
    @PostMapping("/batch")
    public Result<Boolean> batchAddStudents(@Valid @RequestBody List<StudentDTO> dtoList) {
        return Result.success("批量添加成功", studentService.batchAddStudents(dtoList));
    }
}
