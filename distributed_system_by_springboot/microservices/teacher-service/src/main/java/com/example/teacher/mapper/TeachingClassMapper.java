package com.example.teacher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.TeachingClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学班Mapper（用于级联删除）
 */
@Mapper
public interface TeachingClassMapper extends BaseMapper<TeachingClass> {
}
