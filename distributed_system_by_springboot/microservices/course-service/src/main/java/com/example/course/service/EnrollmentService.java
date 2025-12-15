package com.example.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.EnrollmentDTO;
import com.example.common.entity.Enrollment;
import com.example.common.vo.EnrollmentVO;

import java.util.List;

/**
 * 选课服务接口
 */
public interface EnrollmentService {
    
    /**
     * 分页查询选课
     */
    IPage<EnrollmentVO> page(Page<Enrollment> page, String studentId, String classId);
    
    /**
     * 获取学生的选课列表
     */
    List<EnrollmentVO> getByStudentId(String studentId);
    
    /**
     * 获取教学班的选课列表
     */
    List<EnrollmentVO> getByClassId(String classId);
    
    /**
     * 根据ID获取选课
     */
    EnrollmentVO getById(String id);
    
    /**
     * 新增选课
     */
    boolean save(EnrollmentDTO dto);
    
    /**
     * 批量选课
     */
    boolean saveBatch(List<EnrollmentDTO> dtos);
    
    /**
     * 删除选课
     */
    boolean delete(String id);
    
    /**
     * 取消选课
     */
    boolean cancel(String studentId, String classId);
    
    /**
     * 批量删除选课
     */
    boolean deleteBatch(List<String> ids);
    
    /**
     * 检查是否已选课
     */
    boolean exists(String studentId, String classId);
    
    /**
     * 统计选课总数
     */
    long count();
    
    /**
     * 统计教学班选课人数
     */
    long countByClassId(String classId);
}
