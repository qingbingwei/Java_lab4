package com.example.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper（用于关联查询）
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
