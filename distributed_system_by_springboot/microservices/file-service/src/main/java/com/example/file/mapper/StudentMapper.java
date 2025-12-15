package com.example.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
