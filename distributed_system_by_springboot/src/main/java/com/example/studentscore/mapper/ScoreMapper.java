package com.example.studentscore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentscore.entity.Score;
import com.example.studentscore.query.ScoreQuery;
import com.example.studentscore.vo.CourseStatisticsVO;
import com.example.studentscore.vo.ScoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成绩Mapper接口
 *
 * @author system
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    /**
     * 分页查询成绩（带关联信息）
     */
    IPage<ScoreVO> selectScorePage(Page<?> page, @Param("query") ScoreQuery query);

    /**
     * 查询学生成绩列表
     */
    List<ScoreVO> selectStudentScores(@Param("studentDbId") Long studentDbId);

    /**
     * 查询成绩排名
     */
    List<ScoreVO> selectScoreRanking(@Param("query") ScoreQuery query);

    /**
     * 查询课程统计
     */
    List<CourseStatisticsVO> selectCourseStatistics(@Param("semester") String semester);
}
