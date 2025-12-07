package com.example.studentscore.service.impl;

import com.example.studentscore.entity.Score;
import com.example.studentscore.service.*;
import com.example.studentscore.vo.CourseStatisticsVO;
import com.example.studentscore.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 统计服务实现类
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final TeachingClassService teachingClassService;
    private final EnrollmentService enrollmentService;
    private final ScoreService scoreService;

    @Override
    public StatisticsVO getOverviewStatistics() {
        StatisticsVO vo = new StatisticsVO();
        
        // 基础统计
        vo.setStudentCount(studentService.count());
        vo.setTeacherCount(teacherService.count());
        vo.setCourseCount(courseService.count());
        vo.setTeachingClassCount(teachingClassService.count());
        vo.setEnrollmentCount(enrollmentService.count());
        vo.setScoreCount(scoreService.count());
        
        // 成绩统计
        List<Score> allScores = scoreService.list();
        if (!allScores.isEmpty()) {
            List<Score> scoresWithFinal = allScores.stream()
                    .filter(s -> s.getFinalScore() != null)
                    .toList();
            
            if (!scoresWithFinal.isEmpty()) {
                // 平均分
                double avg = scoresWithFinal.stream()
                        .mapToDouble(s -> s.getFinalScore().doubleValue())
                        .average()
                        .orElse(0);
                vo.setAverageScore(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
                
                // 最高分
                double max = scoresWithFinal.stream()
                        .mapToDouble(s -> s.getFinalScore().doubleValue())
                        .max()
                        .orElse(0);
                vo.setMaxScore(BigDecimal.valueOf(max).setScale(2, RoundingMode.HALF_UP));
                
                // 最低分
                double min = scoresWithFinal.stream()
                        .mapToDouble(s -> s.getFinalScore().doubleValue())
                        .min()
                        .orElse(0);
                vo.setMinScore(BigDecimal.valueOf(min).setScale(2, RoundingMode.HALF_UP));
                
                // 及格率
                long passCount = scoresWithFinal.stream()
                        .filter(s -> s.getFinalScore().doubleValue() >= 60)
                        .count();
                double passRate = passCount * 100.0 / scoresWithFinal.size();
                vo.setPassRate(BigDecimal.valueOf(passRate).setScale(2, RoundingMode.HALF_UP));
                
                // 优秀率
                long excellentCount = scoresWithFinal.stream()
                        .filter(s -> s.getFinalScore().doubleValue() >= 90)
                        .count();
                double excellentRate = excellentCount * 100.0 / scoresWithFinal.size();
                vo.setExcellentRate(BigDecimal.valueOf(excellentRate).setScale(2, RoundingMode.HALF_UP));
            }
        }
        
        return vo;
    }

    @Override
    public Map<String, Object> getScoreDistribution(String semester, String courseId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取成绩列表并按分数段统计
        List<Score> scores = scoreService.list();
        List<Score> filtered = scores.stream()
                .filter(s -> s.getFinalScore() != null)
                .toList();
        
        // 分数段统计
        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("优秀(90-100)", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 90).count());
        distribution.put("良好(80-89)", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 80 && s.getFinalScore().doubleValue() < 90).count());
        distribution.put("中等(70-79)", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 70 && s.getFinalScore().doubleValue() < 80).count());
        distribution.put("及格(60-69)", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 60 && s.getFinalScore().doubleValue() < 70).count());
        distribution.put("不及格(<60)", filtered.stream().filter(s -> s.getFinalScore().doubleValue() < 60).count());
        
        result.put("distribution", distribution);
        result.put("labels", distribution.keySet());
        result.put("values", distribution.values());
        
        return result;
    }

    @Override
    public Map<String, Object> getCourseAverageScores(String semester) {
        Map<String, Object> result = new HashMap<>();
        
        List<CourseStatisticsVO> courseStats = scoreService.getCourseStatistics(semester);
        
        List<String> labels = courseStats.stream().map(CourseStatisticsVO::getCourseName).toList();
        List<BigDecimal> values = courseStats.stream().map(CourseStatisticsVO::getAverageScore).toList();
        
        result.put("labels", labels);
        result.put("values", values);
        result.put("details", courseStats);
        
        return result;
    }

    @Override
    public Map<String, Object> getScoreTrend(String studentId) {
        Map<String, Object> result = new HashMap<>();
        
        // 此处简化实现，实际应该按学期统计学生成绩趋势
        result.put("labels", List.of("2022-2023-1", "2022-2023-2", "2023-2024-1"));
        result.put("values", List.of(78.5, 82.3, 85.1));
        
        return result;
    }

    @Override
    public Map<String, Object> getClassComparison(String courseId) {
        Map<String, Object> result = new HashMap<>();
        
        List<CourseStatisticsVO> courseStats = scoreService.getCourseStatistics(null);
        
        List<String> labels = courseStats.stream().map(CourseStatisticsVO::getCourseName).toList();
        List<BigDecimal> avgScores = courseStats.stream().map(CourseStatisticsVO::getAverageScore).toList();
        List<BigDecimal> passRates = courseStats.stream().map(CourseStatisticsVO::getPassRate).toList();
        
        result.put("labels", labels);
        result.put("avgScores", avgScores);
        result.put("passRates", passRates);
        
        return result;
    }
}
