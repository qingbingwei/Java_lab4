package com.example.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.TeachingClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学班Mapper（用于关联查询）
 */
@Mapper
public interface TeachingClassMapper extends BaseMapper<TeachingClass> {
}
