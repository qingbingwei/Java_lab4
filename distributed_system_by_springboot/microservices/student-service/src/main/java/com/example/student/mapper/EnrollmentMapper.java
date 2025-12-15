package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选课Mapper（用于级联删除）
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
}
