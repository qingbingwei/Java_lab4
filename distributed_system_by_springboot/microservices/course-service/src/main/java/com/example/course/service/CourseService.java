package com.example.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.CourseDTO;
import com.example.common.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {
    
    /**
     * 分页查询课程
     */
    IPage<Course> page(Page<Course> page, String courseId, String name, String type);
    
    /**
     * 获取所有课程
     */
    List<Course> list();
    
    /**
     * 根据ID获取课程
     */
    Course getById(String id);
    
    /**
     * 根据课程号获取课程
     */
    Course getByCourseId(String courseId);
    
    /**
     * 新增课程
     */
    boolean save(CourseDTO dto);
    
    /**
     * 更新课程
     */
    boolean update(CourseDTO dto);
    
    /**
     * 删除课程
     */
    boolean delete(String id);
    
    /**
     * 批量删除课程
     */
    boolean deleteBatch(List<String> ids);
    
    /**
     * 检查课程号是否存在
     */
    boolean existsByCourseId(String courseId);
    
    /**
     * 统计课程总数
     */
    long count();
}
