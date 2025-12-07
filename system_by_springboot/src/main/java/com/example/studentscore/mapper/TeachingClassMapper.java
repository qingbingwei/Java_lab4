package com.example.studentscore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.query.TeachingClassQuery;
import com.example.studentscore.vo.TeachingClassVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 教学班Mapper接口
 *
 * @author system
 */
@Mapper
public interface TeachingClassMapper extends BaseMapper<TeachingClass> {

    /**
     * 分页查询教学班（带关联信息）
     */
    IPage<TeachingClassVO> selectTeachingClassPage(Page<?> page, @Param("query") TeachingClassQuery query);

    /**
     * 根据ID查询教学班详情
     */
    TeachingClassVO selectTeachingClassById(@Param("id") Long id);

    /**
     * 查询所有教学班列表（带关联信息）
     */
    List<TeachingClassVO> selectAllTeachingClasses();
}
