package com.example.studentscore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentscore.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper接口
 *
 * @author system
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
