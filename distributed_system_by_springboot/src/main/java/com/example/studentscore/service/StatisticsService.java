package com.example.studentscore.service;

import com.example.studentscore.vo.StatisticsVO;

import java.util.Map;

/**
 * 统计服务接口
 *
 * @author system
 */
public interface StatisticsService {

    /**
     * 获取系统概览统计
     */
    StatisticsVO getOverviewStatistics();

    /**
     * 获取成绩分布统计
     */
    Map<String, Object> getScoreDistribution(String semester, String courseId);

    /**
     * 获取各课程平均分统计
     */
    Map<String, Object> getCourseAverageScores(String semester);

    /**
     * 获取成绩趋势统计
     */
    Map<String, Object> getScoreTrend(String studentId);

    /**
     * 获取班级成绩对比
     */
    Map<String, Object> getClassComparison(String courseId);
}
