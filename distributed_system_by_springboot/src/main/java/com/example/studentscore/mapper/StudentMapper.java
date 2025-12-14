package com.example.studentscore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentscore.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口
 *
 * @author system
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
