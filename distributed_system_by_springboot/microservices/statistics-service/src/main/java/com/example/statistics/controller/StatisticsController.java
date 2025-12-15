package com.example.statistics.controller;

import com.example.common.result.Result;
import com.example.common.vo.ClassStatisticsVO;
import com.example.common.vo.CourseStatisticsVO;
import com.example.common.vo.StatisticsVO;
import com.example.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 */
@Tag(name = "数据统计", description = "数据统计分析相关接口")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "获取系统概览统计")
    @GetMapping("/overview")
    public Result<StatisticsVO> getOverview() {
        return Result.success(statisticsService.getOverview());
    }
    
    @Operation(summary = "获取课程统计")
    @GetMapping("/courses")
    public Result<List<CourseStatisticsVO>> getCourseStatistics() {
        return Result.success(statisticsService.getCourseStatistics());
    }
    
    @Operation(summary = "获取教学班统计")
    @GetMapping("/classes")
    public Result<List<ClassStatisticsVO>> getClassStatistics() {
        return Result.success(statisticsService.getClassStatistics());
    }
    
    @Operation(summary = "获取指定课程的统计")
    @GetMapping("/courses/{courseId}")
    public Result<CourseStatisticsVO> getCourseStatisticsById(
            @Parameter(description = "课程号") @PathVariable("courseId") String courseId) {
        CourseStatisticsVO vo = statisticsService.getCourseStatisticsById(courseId);
        return vo != null ? Result.success(vo) : Result.error("课程不存在");
    }
    
    @Operation(summary = "获取指定教学班的统计")
    @GetMapping("/classes/{classId}")
    public Result<ClassStatisticsVO> getClassStatisticsById(
            @Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        ClassStatisticsVO vo = statisticsService.getClassStatisticsById(classId);
        return vo != null ? Result.success(vo) : Result.error("教学班不存在");
    }
    
    @Operation(summary = "获取成绩分布")
    @GetMapping("/distribution")
    public Result<List<Integer>> getScoreDistribution() {
        return Result.success(statisticsService.getScoreDistribution());
    }
    
    @Operation(summary = "获取教学班成绩分布")
    @GetMapping("/distribution/{classId}")
    public Result<List<Integer>> getClassScoreDistribution(
            @Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        return Result.success(statisticsService.getClassScoreDistribution(classId));
    }
    
    // ===== 以下是为前端统计页面添加的接口 =====
    
    @Operation(summary = "获取成绩分布（前端统计页面用）")
    @GetMapping("/score-distribution")
    public Result<Map<String, Object>> getScoreDistributionForFrontend() {
        List<Integer> distribution = statisticsService.getScoreDistribution();
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> distMap = new HashMap<>();
        // distribution 返回5个等级的人数: [<60, 60-69, 70-79, 80-89, 90-100]
        if (distribution.size() >= 5) {
            distMap.put("不及格(<60)", distribution.get(0));
            distMap.put("及格(60-69)", distribution.get(1));
            distMap.put("中等(70-79)", distribution.get(2));
            distMap.put("良好(80-89)", distribution.get(3));
            distMap.put("优秀(90-100)", distribution.get(4));
        }
        result.put("distribution", distMap);
        return Result.success(result);
    }
    
    @Operation(summary = "获取课程平均分（前端统计页面用）")
    @GetMapping("/course-average")
    public Result<Map<String, Object>> getCourseAverageForFrontend() {
        List<CourseStatisticsVO> courses = statisticsService.getCourseStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("details", courses);
        return Result.success(result);
    }
    
    @Operation(summary = "获取班级对比（前端统计页面用）")
    @GetMapping("/class-comparison")
    public Result<Map<String, Object>> getClassComparisonForFrontend() {
        List<ClassStatisticsVO> classes = statisticsService.getClassStatistics();
        Map<String, Object> result = new HashMap<>();
        result.put("labels", classes.stream().map(ClassStatisticsVO::getClassName).toList());
        result.put("avgScores", classes.stream().map(c -> c.getAverageScore() != null ? c.getAverageScore().doubleValue() : 0.0).toList());
        result.put("passRates", classes.stream().map(c -> c.getPassRate() != null ? c.getPassRate().doubleValue() : 0.0).toList());
        return Result.success(result);
    }
}
