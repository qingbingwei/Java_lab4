package com.example.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选课Mapper
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
}
