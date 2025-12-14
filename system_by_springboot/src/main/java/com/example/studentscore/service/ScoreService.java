package com.example.studentscore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.ScoreDTO;
import com.example.studentscore.entity.Score;
import com.example.studentscore.query.ScoreQuery;
import com.example.studentscore.vo.CourseStatisticsVO;
import com.example.studentscore.vo.ScoreVO;

import java.util.List;

/**
 * 成绩服务接口
 *
 * @author system
 */
public interface ScoreService extends IService<Score> {

    /**
     * 分页查询成绩
     */
    IPage<ScoreVO> pageScores(ScoreQuery query);

    /**
     * 查询学生成绩列表
     */
    List<ScoreVO> getStudentScores(Long studentDbId);

    /**
     * 查询学生在某教学班的成绩
     */
    Score getByStudentAndClass(Long studentDbId, Long teachingClassDbId);

    /**
     * 录入成绩，返回成绩ID
     */
    Long addScore(ScoreDTO dto);

    /**
     * 更新成绩
     */
    boolean updateScore(ScoreDTO dto);

    /**
     * 批量录入成绩
     */
    boolean batchAddScores(List<ScoreDTO> dtoList);

    /**
     * 获取成绩排名
     */
    List<ScoreVO> getScoreRanking(ScoreQuery query);

    /**
     * 获取课程统计
     */
    List<CourseStatisticsVO> getCourseStatistics(String semester);
}
