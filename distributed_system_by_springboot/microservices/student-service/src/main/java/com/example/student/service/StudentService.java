package com.example.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.dto.StudentDTO;
import com.example.common.entity.Student;
import com.example.common.query.StudentQuery;
import com.example.common.vo.StudentScoreDetailVO;

import java.util.List;

/**
 * 学生服务接口
 */
public interface StudentService {
    
    /**
     * 分页查询学生
     */
    IPage<Student> page(StudentQuery query);
    
    /**
     * 获取所有学生
     */
    List<Student> list();
    
    /**
     * 根据ID获取学生
     */
    Student getById(String id);
    
    /**
     * 根据学号获取学生
     */
    Student getByStudentId(String studentId);
    
    /**
     * 新增学生
     */
    boolean save(StudentDTO dto);
    
    /**
     * 更新学生
     */
    boolean update(StudentDTO dto);
    
    /**
     * 删除学生
     */
    boolean delete(String id);
    
    /**
     * 批量删除学生
     */
    boolean deleteBatch(List<String> ids);
    
    /**
     * 检查学号是否存在
     */
    boolean existsByStudentId(String studentId);
    
    /**
     * 获取学生成绩详情
     */
    StudentScoreDetailVO getScoreDetail(String studentId);
    
    /**
     * 统计学生总数
     */
    long count();
}
