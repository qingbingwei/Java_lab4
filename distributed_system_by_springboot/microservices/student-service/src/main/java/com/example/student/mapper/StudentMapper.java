package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
