package com.example.studentscore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.EnrollmentDTO;
import com.example.studentscore.entity.Enrollment;
import com.example.studentscore.vo.EnrollmentVO;

import java.util.List;

/**
 * 选课服务接口
 *
 * @author system
 */
public interface EnrollmentService extends IService<Enrollment> {

    /**
     * 分页查询选课记录
     */
    IPage<Enrollment> pageEnrollments(Integer pageNum, Integer pageSize, Long studentDbId, Long teachingClassDbId);

    /**
     * 学生选课
     */
    boolean enroll(EnrollmentDTO dto);

    /**
     * 学生退课
     */
    boolean drop(Long studentDbId, Long teachingClassDbId);

    /**
     * 查询学生已选课程（带关联信息）
     */
    List<EnrollmentVO> getStudentEnrollments(Long studentDbId);

    /**
     * 查询教学班学生列表（带关联信息）
     */
    List<EnrollmentVO> getClassEnrollments(Long teachingClassDbId);

    /**
     * 检查是否已选课
     */
    boolean isEnrolled(Long studentDbId, Long teachingClassDbId);

    /**
     * 批量选课
     */
    boolean batchEnroll(List<EnrollmentDTO> dtoList);
}
