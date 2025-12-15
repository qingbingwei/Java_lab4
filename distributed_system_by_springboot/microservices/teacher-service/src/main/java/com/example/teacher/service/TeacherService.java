package com.example.teacher.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.TeacherDTO;
import com.example.common.entity.Teacher;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService {
    
    /**
     * 分页查询教师
     */
    IPage<Teacher> page(Page<Teacher> page, String teacherId, String name, String department);
    
    /**
     * 获取所有教师
     */
    List<Teacher> list();
    
    /**
     * 根据ID获取教师
     */
    Teacher getById(String id);
    
    /**
     * 根据工号获取教师
     */
    Teacher getByTeacherId(String teacherId);
    
    /**
     * 新增教师
     */
    boolean save(TeacherDTO dto);
    
    /**
     * 更新教师
     */
    boolean update(TeacherDTO dto);
    
    /**
     * 删除教师
     */
    boolean delete(String id);
    
    /**
     * 批量删除教师
     */
    boolean deleteBatch(List<String> ids);
    
    /**
     * 检查工号是否存在
     */
    boolean existsByTeacherId(String teacherId);
    
    /**
     * 统计教师总数
     */
    long count();
}
