package com.example.statistics.service;

import com.example.common.vo.ClassStatisticsVO;
import com.example.common.vo.CourseStatisticsVO;
import com.example.common.vo.StatisticsVO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取系统概览统计
     */
    StatisticsVO getOverview();
    
    /**
     * 获取课程统计
     */
    List<CourseStatisticsVO> getCourseStatistics();
    
    /**
     * 获取教学班统计
     */
    List<ClassStatisticsVO> getClassStatistics();
    
    /**
     * 获取指定课程的统计
     */
    CourseStatisticsVO getCourseStatisticsById(String courseId);
    
    /**
     * 获取指定教学班的统计
     */
    ClassStatisticsVO getClassStatisticsById(String classId);
    
    /**
     * 获取成绩分布
     */
    List<Integer> getScoreDistribution();
    
    /**
     * 获取教学班成绩分布
     */
    List<Integer> getClassScoreDistribution(String classId);
}
