package com.example.studentscore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.TeacherDTO;
import com.example.studentscore.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 *
 * @author system
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师
     */
    IPage<Teacher> pageTeachers(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 根据教师编号查询
     */
    Teacher getByTeacherId(String teacherId);

    /**
     * 添加教师
     */
    boolean addTeacher(TeacherDTO dto);

    /**
     * 更新教师
     */
    boolean updateTeacher(TeacherDTO dto);

    /**
     * 批量添加教师
     */
    boolean batchAddTeachers(List<TeacherDTO> dtoList);
}
