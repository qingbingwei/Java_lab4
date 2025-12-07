package com.example.studentscore.controller;

import com.example.studentscore.common.Result;
import com.example.studentscore.service.StatisticsService;
import com.example.studentscore.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计分析控制器
 *
 * @author system
 */
@Tag(name = "统计分析", description = "数据统计与分析接口")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取系统概览统计")
    @GetMapping("/overview")
    public Result<StatisticsVO> getOverviewStatistics() {
        return Result.success(statisticsService.getOverviewStatistics());
    }

    @Operation(summary = "获取成绩分布统计")
    @GetMapping("/score-distribution")
    public Result<Map<String, Object>> getScoreDistribution(
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "课程编号") @RequestParam(required = false) String courseId) {
        return Result.success(statisticsService.getScoreDistribution(semester, courseId));
    }

    @Operation(summary = "获取各课程平均分统计")
    @GetMapping("/course-average")
    public Result<Map<String, Object>> getCourseAverageScores(
            @Parameter(description = "学期") @RequestParam(required = false) String semester) {
        return Result.success(statisticsService.getCourseAverageScores(semester));
    }

    @Operation(summary = "获取学生成绩趋势")
    @GetMapping("/score-trend")
    public Result<Map<String, Object>> getScoreTrend(
            @Parameter(description = "学号") @RequestParam String studentId) {
        return Result.success(statisticsService.getScoreTrend(studentId));
    }

    @Operation(summary = "获取班级成绩对比")
    @GetMapping("/class-comparison")
    public Result<Map<String, Object>> getClassComparison(
            @Parameter(description = "课程编号") @RequestParam(required = false) String courseId) {
        return Result.success(statisticsService.getClassComparison(courseId));
    }
}
