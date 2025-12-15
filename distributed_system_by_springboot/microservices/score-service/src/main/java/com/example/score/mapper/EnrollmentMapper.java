package com.example.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选课Mapper（用于关联查询）
 */
@Mapper
public interface EnrollmentMapper extends BaseMapper<Enrollment> {
}
