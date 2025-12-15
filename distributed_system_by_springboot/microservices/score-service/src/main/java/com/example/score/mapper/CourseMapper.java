package com.example.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程Mapper（用于关联查询）
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
