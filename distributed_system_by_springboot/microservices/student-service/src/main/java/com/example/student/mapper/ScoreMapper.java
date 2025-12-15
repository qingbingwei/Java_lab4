package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Score;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩Mapper（用于级联删除）
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
}
