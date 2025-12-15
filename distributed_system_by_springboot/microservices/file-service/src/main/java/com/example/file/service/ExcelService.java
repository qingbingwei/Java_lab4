package com.example.file.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Excel服务接口
 */
public interface ExcelService {
    
    /**
     * 导入学生数据
     */
    Map<String, Object> importStudents(MultipartFile file);
    
    /**
     * 导出学生数据
     */
    void exportStudents(HttpServletResponse response);
    
    /**
     * 导入教师数据
     */
    Map<String, Object> importTeachers(MultipartFile file);
    
    /**
     * 导出教师数据
     */
    void exportTeachers(HttpServletResponse response);
    
    /**
     * 导入课程数据
     */
    Map<String, Object> importCourses(MultipartFile file);
    
    /**
     * 导出课程数据
     */
    void exportCourses(HttpServletResponse response);
    
    /**
     * 导入成绩数据
     */
    Map<String, Object> importScores(MultipartFile file);
    
    /**
     * 导出成绩数据
     * @param classId 教学班编号
     * @param studentDbId 学生数据库ID（学生只导出自己成绩）
     * @param teacherDbId 教师数据库ID（教师只导出自己教学班成绩）
     * @param response HTTP响应
     */
    void exportScores(String classId, Long studentDbId, Long teacherDbId, HttpServletResponse response);
    
    /**
     * 下载导入模板
     */
    void downloadTemplate(String type, HttpServletResponse response);
}
