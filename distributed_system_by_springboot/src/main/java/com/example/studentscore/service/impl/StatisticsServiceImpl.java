package com.example.studentscore.service.impl;

import com.example.studentscore.entity.Score;
import com.example.studentscore.service.*;
import com.example.studentscore.vo.ClassStatisticsVO;
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
        // 获取成绩列表并按分数段统计
        List<Score> scores = scoreService.list();
        List<Score> filtered = scores.stream()
                .filter(s -> s.getFinalScore() != null)
                .toList();

        // 分数段统计 - 使用前端期望的key格式
        Map<String, Object> distribution = new LinkedHashMap<>();
        distribution.put("90-100", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 90).count());
        distribution.put("80-89", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 80 && s.getFinalScore().doubleValue() < 90).count());
        distribution.put("70-79", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 70 && s.getFinalScore().doubleValue() < 80).count());
        distribution.put("60-69", filtered.stream().filter(s -> s.getFinalScore().doubleValue() >= 60 && s.getFinalScore().doubleValue() < 70).count());
        distribution.put("0-59", filtered.stream().filter(s -> s.getFinalScore().doubleValue() < 60).count());

        return distribution;
    }

    @Override
    public Map<String, Object> getCourseAverageScores(String semester) {
        List<CourseStatisticsVO> courseStats = scoreService.getCourseStatistics(semester);

        // 直接返回课程统计列表，前端会自己提取需要的字段
        Map<String, Object> result = new HashMap<>();
        result.put("data", courseStats);

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
        // 获取所有成绩数据
        List<Score> allScores = scoreService.list();

        // 按学生ID分组获取学生信息
        List<Long> studentIds = allScores.stream()
                .map(Score::getStudentDbId)
                .distinct()
                .toList();

        Map<Long, String> studentClassMap = studentService.listByIds(studentIds).stream()
                .collect(HashMap::new, (m, s) -> m.put(s.getId(), s.getClassName()), HashMap::putAll);

        // 按班级分组统计
        Map<String, List<Score>> scoresByClass = allScores.stream()
                .filter(s -> s.getFinalScore() != null && studentClassMap.containsKey(s.getStudentDbId()))
                .collect(java.util.stream.Collectors.groupingBy(
                        s -> studentClassMap.get(s.getStudentDbId())
                ));

        // 计算每个班级的统计数据
        List<ClassStatisticsVO> classStats = new ArrayList<>();
        for (Map.Entry<String, List<Score>> entry : scoresByClass.entrySet()) {
            String className = entry.getKey();
            List<Score> scores = entry.getValue();

            if (scores.isEmpty()) continue;

            ClassStatisticsVO vo = new ClassStatisticsVO();
            vo.setClassName(className);
            vo.setStudentCount((long) scores.size());

            // 平均分
            double avg = scores.stream()
                    .mapToDouble(s -> s.getFinalScore().doubleValue())
                    .average()
                    .orElse(0);
            vo.setAvgScore(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));

            // 最高分
            double max = scores.stream()
                    .mapToDouble(s -> s.getFinalScore().doubleValue())
                    .max()
                    .orElse(0);
            vo.setMaxScore(BigDecimal.valueOf(max).setScale(2, RoundingMode.HALF_UP));

            // 最低分
            double min = scores.stream()
                    .mapToDouble(s -> s.getFinalScore().doubleValue())
                    .min()
                    .orElse(0);
            vo.setMinScore(BigDecimal.valueOf(min).setScale(2, RoundingMode.HALF_UP));

            // 及格人数和及格率
            long passCount = scores.stream()
                    .filter(s -> s.getFinalScore().doubleValue() >= 60)
                    .count();
            vo.setPassCount(passCount);

            double passRate = passCount * 100.0 / scores.size();
            vo.setPassRate(BigDecimal.valueOf(passRate).setScale(2, RoundingMode.HALF_UP));

            classStats.add(vo);
        }

        // 按班级名称排序
        classStats.sort(Comparator.comparing(ClassStatisticsVO::getClassName));

        Map<String, Object> result = new HashMap<>();
        result.put("data", classStats);

        return result;
    }
}
