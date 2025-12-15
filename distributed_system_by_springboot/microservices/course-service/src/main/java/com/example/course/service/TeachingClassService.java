package com.example.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.dto.TeachingClassDTO;
import com.example.common.entity.TeachingClass;
import com.example.common.query.TeachingClassQuery;
import com.example.common.vo.ClassStudentVO;
import com.example.common.vo.TeachingClassVO;

import java.util.List;

/**
 * 教学班服务接口
 */
public interface TeachingClassService {
    
    /**
     * 分页查询教学班
     */
    IPage<TeachingClassVO> page(TeachingClassQuery query);
    
    /**
     * 获取所有教学班
     */
    List<TeachingClassVO> list();
    
    /**
     * 根据ID获取教学班
     */
    TeachingClassVO getById(String id);
    
    /**
     * 根据教学班编号获取教学班
     */
    TeachingClassVO getByClassId(String classId);
    
    /**
     * 根据教师工号获取教学班
     */
    List<TeachingClassVO> getByTeacherId(String teacherId);
    
    /**
     * 获取教学班学生列表（含成绩）
     */
    List<ClassStudentVO> getStudents(Long classDbId);
    
    /**
     * 新增教学班
     */
    boolean save(TeachingClassDTO dto);
    
    /**
     * 更新教学班
     */
    boolean update(TeachingClassDTO dto);
    
    /**
     * 删除教学班
     */
    boolean delete(String id);
    
    /**
     * 批量删除教学班
     */
    boolean deleteBatch(List<String> ids);
    
    /**
     * 检查教学班编号是否存在
     */
    boolean existsByClassId(String classId);
    
    /**
     * 统计教学班总数
     */
    long count();
}
