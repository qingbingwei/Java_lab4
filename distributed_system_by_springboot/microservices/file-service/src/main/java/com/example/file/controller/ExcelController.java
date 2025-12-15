package com.example.file.controller;

import com.example.common.result.Result;
import com.example.file.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Excel控制器
 */
@Tag(name = "Excel导入导出", description = "Excel数据导入导出相关接口")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {
    
    private final ExcelService excelService;
    
    @Operation(summary = "导入学生数据")
    @PostMapping("/import/students")
    public Result<Map<String, Object>> importStudents(@Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        return Result.success(excelService.importStudents(file));
    }
    
    @Operation(summary = "导出学生数据")
    @GetMapping("/export/students")
    public void exportStudents(HttpServletResponse response) {
        excelService.exportStudents(response);
    }
    
    @Operation(summary = "导入教师数据")
    @PostMapping("/import/teachers")
    public Result<Map<String, Object>> importTeachers(@Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        return Result.success(excelService.importTeachers(file));
    }
    
    @Operation(summary = "导出教师数据")
    @GetMapping("/export/teachers")
    public void exportTeachers(HttpServletResponse response) {
        excelService.exportTeachers(response);
    }
    
    @Operation(summary = "导入课程数据")
    @PostMapping("/import/courses")
    public Result<Map<String, Object>> importCourses(@Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        return Result.success(excelService.importCourses(file));
    }
    
    @Operation(summary = "导出课程数据")
    @GetMapping("/export/courses")
    public void exportCourses(HttpServletResponse response) {
        excelService.exportCourses(response);
    }
    
    @Operation(summary = "导入成绩数据")
    @PostMapping("/import/scores")
    public Result<Map<String, Object>> importScores(@Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        return Result.success(excelService.importScores(file));
    }
    
    @Operation(summary = "导出成绩数据")
    @GetMapping("/export/scores")
    public void exportScores(
            @Parameter(description = "教学班编号") @RequestParam(required = false) String classId,
            HttpServletResponse response) {
        excelService.exportScores(classId, response);
    }
    
    @Operation(summary = "下载导入模板")
    @GetMapping("/template/{type}")
    public void downloadTemplate(
            @Parameter(description = "模板类型: student, teacher, course, score") @PathVariable("type") String type,
            HttpServletResponse response) {
        excelService.downloadTemplate(type, response);
    }
}
