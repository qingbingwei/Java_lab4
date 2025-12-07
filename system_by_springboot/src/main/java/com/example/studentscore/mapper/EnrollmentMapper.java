package com.example.studentscore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentscore.entity.Enrollment;
import com.example.studentscore.vo.EnrollmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 选课Mapper接口
 *
 * @author system
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
    
    /**
     * 查询学生选课列表（带关联信息）
     */
    List<EnrollmentVO> selectStudentEnrollments(@Param("studentDbId") Long studentDbId);
    
    /**
     * 查询教学班学生列表（带关联信息）
     */
    List<EnrollmentVO> selectClassEnrollments(@Param("teachingClassDbId") Long teachingClassDbId);
}
