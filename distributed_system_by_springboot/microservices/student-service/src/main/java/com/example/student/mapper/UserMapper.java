package com.example.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper（用于级联删除）
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
