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
    
    /**
     * 查询已删除的选课记录（绕过逻辑删除过滤）
     * 用于退课后重新选课的场景
     */
    @org.apache.ibatis.annotations.Select("SELECT * FROM enrollment WHERE student_db_id = #{studentDbId} AND teaching_class_db_id = #{teachingClassDbId} AND deleted = 1")
    Enrollment selectDeletedEnrollment(@Param("studentDbId") Long studentDbId, @Param("teachingClassDbId") Long teachingClassDbId);
    
    /**
     * 直接更新记录（绕过逻辑删除过滤）
     * 用于恢复已删除的选课记录
     */
    @org.apache.ibatis.annotations.Update("UPDATE enrollment SET deleted = 0, enroll_time = #{enrollTime}, update_time = #{updateTime} WHERE id = #{id}")
    int restoreEnrollment(@Param("id") Long id, @Param("enrollTime") java.time.LocalDateTime enrollTime, @Param("updateTime") java.time.LocalDateTime updateTime);
}
