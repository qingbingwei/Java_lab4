package com.example.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.dto.StudentDTO;
import com.example.common.entity.Student;
import com.example.common.query.StudentQuery;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.common.vo.StudentScoreDetailVO;
import com.example.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生控制器
 */
@Tag(name = "学生管理", description = "学生信息管理相关接口")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    @Operation(summary = "分页查询学生")
    @GetMapping("/page")
    public Result<PageResult<Student>> page(StudentQuery query) {
        IPage<Student> page = studentService.page(query);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
    
    @Operation(summary = "获取所有学生")
    @GetMapping("/list")
    public Result<List<Student>> list() {
        return Result.success(studentService.list());
    }
    
    @Operation(summary = "根据ID获取学生")
    @GetMapping("/{id}")
    public Result<Student> getById(@Parameter(description = "学生ID") @PathVariable("id") String id) {
        Student student = studentService.getById(id);
        return student != null ? Result.success(student) : Result.error("学生不存在");
    }
    
    @Operation(summary = "根据学号获取学生")
    @GetMapping("/studentId/{studentId}")
    public Result<Student> getByStudentId(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        Student student = studentService.getByStudentId(studentId);
        return student != null ? Result.success(student) : Result.error("学生不存在");
    }
    
    @Operation(summary = "新增学生")
    @PostMapping
    public Result<Boolean> save(@RequestBody StudentDTO dto) {
        return Result.success(studentService.save(dto));
    }
    
    @Operation(summary = "更新学生")
    @PutMapping
    public Result<Boolean> update(@RequestBody StudentDTO dto) {
        return Result.success(studentService.update(dto));
    }
    
    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "学生ID") @PathVariable("id") String id) {
        return Result.success(studentService.delete(id));
    }
    
    @Operation(summary = "批量删除学生")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(studentService.deleteBatch(ids));
    }
    
    @Operation(summary = "检查学号是否存在")
    @GetMapping("/exists/{studentId}")
    public Result<Boolean> existsByStudentId(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        return Result.success(studentService.existsByStudentId(studentId));
    }
    
    @Operation(summary = "获取学生成绩详情")
    @GetMapping("/{studentId}/scores")
    public Result<StudentScoreDetailVO> getScoreDetail(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        return Result.success(studentService.getScoreDetail(studentId));
    }
    
    @Operation(summary = "统计学生总数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(studentService.count());
    }
}
