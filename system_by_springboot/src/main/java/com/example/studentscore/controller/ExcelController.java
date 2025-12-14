package com.example.studentscore.controller;

import com.example.studentscore.common.Result;
import com.example.studentscore.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Excel导入导出控制器
 */
@Tag(name = "Excel导入导出", description = "Excel数据导入导出接口")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    // ==================== 学生相关 ====================

    @Operation(summary = "导出学生数据")
    @GetMapping("/students/export")
    public void exportStudents(
            HttpServletResponse response,
            @Parameter(description = "班级名称") @RequestParam(required = false) String className,
            @Parameter(description = "年级") @RequestParam(required = false) String grade) {
        excelService.exportStudents(response, className, grade);
    }

    @Operation(summary = "导入学生数据")
    @PostMapping("/students/import")
    public Result<Integer> importStudents(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        int count = excelService.importStudents(file);
        return Result.success("成功导入 " + count + " 条学生数据", count);
    }

    @Operation(summary = "下载学生导入模板")
    @GetMapping("/students/template")
    public void downloadStudentTemplate(HttpServletResponse response) {
        excelService.downloadStudentTemplate(response);
    }

    // ==================== 成绩相关 ====================

    @Operation(summary = "导出成绩数据")
    @GetMapping("/scores/export")
    public void exportScores(
            HttpServletResponse response,
            @Parameter(description = "教学班ID") @RequestParam(required = false) Long teachingClassId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "学生数据库ID（学生角色专用）") @RequestParam(required = false) Long studentDbId) {
        excelService.exportScores(response, teachingClassId, semester, studentDbId);
    }

    @Operation(summary = "导入成绩数据")
    @PostMapping("/scores/import")
    public Result<Integer> importScores(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        int count = excelService.importScores(file);
        return Result.success("成功导入 " + count + " 条成绩数据", count);
    }

    @Operation(summary = "下载成绩导入模板")
    @GetMapping("/scores/template")
    public void downloadScoreTemplate(HttpServletResponse response) {
        excelService.downloadScoreTemplate(response);
    }

    // ==================== 教师相关 ====================

    @Operation(summary = "导出教师数据")
    @GetMapping("/teachers/export")
    public void exportTeachers(HttpServletResponse response) {
        excelService.exportTeachers(response);
    }

    @Operation(summary = "导入教师数据")
    @PostMapping("/teachers/import")
    public Result<Integer> importTeachers(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        int count = excelService.importTeachers(file);
        return Result.success("成功导入 " + count + " 条教师数据", count);
    }

    @Operation(summary = "下载教师导入模板")
    @GetMapping("/teachers/template")
    public void downloadTeacherTemplate(HttpServletResponse response) {
        excelService.downloadTeacherTemplate(response);
    }

    // ==================== 课程相关 ====================

    @Operation(summary = "导出课程数据")
    @GetMapping("/courses/export")
    public void exportCourses(HttpServletResponse response) {
        excelService.exportCourses(response);
    }

    @Operation(summary = "导入课程数据")
    @PostMapping("/courses/import")
    public Result<Integer> importCourses(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        int count = excelService.importCourses(file);
        return Result.success("成功导入 " + count + " 条课程数据", count);
    }

    @Operation(summary = "下载课程导入模板")
    @GetMapping("/courses/template")
    public void downloadCourseTemplate(HttpServletResponse response) {
        excelService.downloadCourseTemplate(response);
    }
}
