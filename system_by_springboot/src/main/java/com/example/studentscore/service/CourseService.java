package com.example.studentscore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.CourseDTO;
import com.example.studentscore.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 *
 * @author system
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程
     */
    IPage<Course> pageCourses(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 根据课程编号查询
     */
    Course getByCourseId(String courseId);

    /**
     * 添加课程
     */
    boolean addCourse(CourseDTO dto);

    /**
     * 更新课程
     */
    boolean updateCourse(CourseDTO dto);

    /**
     * 批量添加课程
     */
    boolean batchAddCourses(List<CourseDTO> dtoList);
}
