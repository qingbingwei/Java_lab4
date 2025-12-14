package com.example.studentscore.service;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Excel服务接口
 */
public interface ExcelService {

    /**
     * 导出学生数据
     */
    void exportStudents(HttpServletResponse response, String className, String grade);

    /**
     * 导入学生数据
     */
    int importStudents(MultipartFile file);

    /**
     * 下载学生导入模板
     */
    void downloadStudentTemplate(HttpServletResponse response);

    /**
     * 导出成绩数据
     */
    void exportScores(HttpServletResponse response, Long teachingClassId, String semester, Long studentDbId);

    /**
     * 导入成绩数据
     */
    int importScores(MultipartFile file);

    /**
     * 下载成绩导入模板
     */
    void downloadScoreTemplate(HttpServletResponse response);

    /**
     * 导出教师数据
     */
    void exportTeachers(HttpServletResponse response);

    /**
     * 导入教师数据
     */
    int importTeachers(MultipartFile file);

    /**
     * 下载教师导入模板
     */
    void downloadTeacherTemplate(HttpServletResponse response);

    /**
     * 导出课程数据
     */
    void exportCourses(HttpServletResponse response);

    /**
     * 导入课程数据
     */
    int importCourses(MultipartFile file);

    /**
     * 下载课程导入模板
     */
    void downloadCourseTemplate(HttpServletResponse response);
}
