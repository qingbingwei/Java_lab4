package com.example.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
