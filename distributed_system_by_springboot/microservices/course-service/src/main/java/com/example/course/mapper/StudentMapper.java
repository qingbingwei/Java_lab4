package com.example.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper（用于关联查询）
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
