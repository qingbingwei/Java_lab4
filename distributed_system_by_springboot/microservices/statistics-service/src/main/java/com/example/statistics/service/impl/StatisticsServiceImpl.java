package com.example.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.*;
import com.example.common.vo.ClassStatisticsVO;
import com.example.common.vo.CourseStatisticsVO;
import com.example.common.vo.StatisticsVO;
import com.example.statistics.mapper.*;
import com.example.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final TeachingClassMapper teachingClassMapper;
    private final ScoreMapper scoreMapper;
    private final EnrollmentMapper enrollmentMapper;
    
    @Override
    public StatisticsVO getOverview() {
        StatisticsVO vo = new StatisticsVO();
        
        // 统计各实体数量
        vo.setStudentCount(studentMapper.selectCount(null));
        vo.setTeacherCount(teacherMapper.selectCount(null));
        vo.setCourseCount(courseMapper.selectCount(null));
        vo.setTeachingClassCount(teachingClassMapper.selectCount(null));
        vo.setEnrollmentCount(enrollmentMapper.selectCount(null));
        vo.setScoreCount(scoreMapper.selectCount(null));
        
        // 计算平均分
        List<Score> scores = scoreMapper.selectList(null);
        if (!scores.isEmpty()) {
            List<BigDecimal> finalScores = scores.stream()
                    .filter(s -> s.getFinalScore() != null)
                    .map(Score::getFinalScore)
                    .collect(Collectors.toList());
            
            if (!finalScores.isEmpty()) {
                BigDecimal totalScore = finalScores.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setAverageScore(totalScore.divide(BigDecimal.valueOf(finalScores.size()), 2, RoundingMode.HALF_UP));
                
                // 计算及格率
                long passCount = finalScores.stream()
                        .filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0)
                        .count();
                vo.setPassRate(BigDecimal.valueOf(passCount * 100.0 / finalScores.size())
                        .setScale(2, RoundingMode.HALF_UP));
                
                // 计算优秀率
                long excellentCount = finalScores.stream()
                        .filter(s -> s.compareTo(BigDecimal.valueOf(90)) >= 0)
                        .count();
                vo.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / finalScores.size())
                        .setScale(2, RoundingMode.HALF_UP));
            } else {
                vo.setAverageScore(BigDecimal.ZERO);
                vo.setPassRate(BigDecimal.ZERO);
                vo.setExcellentRate(BigDecimal.ZERO);
            }
        } else {
            vo.setAverageScore(BigDecimal.ZERO);
            vo.setPassRate(BigDecimal.ZERO);
            vo.setExcellentRate(BigDecimal.ZERO);
        }
        
        return vo;
    }
    
    @Override
    public StatisticsVO getOverviewByTeacher(Long teacherDbId) {
        StatisticsVO vo = new StatisticsVO();
        
        // 查询该教师的所有教学班
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getTeacherDbId, teacherDbId);
        List<TeachingClass> teacherClasses = teachingClassMapper.selectList(tcWrapper);
        
        if (teacherClasses.isEmpty()) {
            vo.setStudentCount(0L);
            vo.setTeacherCount(1L);
            vo.setCourseCount(0L);
            vo.setTeachingClassCount(0L);
            vo.setEnrollmentCount(0L);
            vo.setScoreCount(0L);
            vo.setAverageScore(BigDecimal.ZERO);
            vo.setPassRate(BigDecimal.ZERO);
            vo.setExcellentRate(BigDecimal.ZERO);
            return vo;
        }
        
        List<Long> classDbIds = teacherClasses.stream().map(TeachingClass::getId).toList();
        
        // 统计该教师的教学班数
        vo.setTeachingClassCount((long) teacherClasses.size());
        
        // 统计相关课程数（去重）
        Set<Long> courseDbIds = teacherClasses.stream().map(TeachingClass::getCourseDbId).collect(Collectors.toSet());
        vo.setCourseCount((long) courseDbIds.size());
        
        // 统计选课人数
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.in(Enrollment::getTeachingClassDbId, classDbIds);
        long enrollmentCount = enrollmentMapper.selectCount(enrollmentWrapper);
        vo.setEnrollmentCount(enrollmentCount);
        
        // 统计学生人数（去重）
        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollmentWrapper);
        Set<Long> studentDbIds = enrollments.stream().map(Enrollment::getStudentDbId).collect(Collectors.toSet());
        vo.setStudentCount((long) studentDbIds.size());
        
        vo.setTeacherCount(1L);
        
        // 统计成绩
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.in(Score::getTeachingClassDbId, classDbIds);
        List<Score> scores = scoreMapper.selectList(scoreWrapper);
        vo.setScoreCount((long) scores.size());
        
        if (!scores.isEmpty()) {
            List<BigDecimal> finalScores = scores.stream()
                    .filter(s -> s.getFinalScore() != null)
                    .map(Score::getFinalScore)
                    .collect(Collectors.toList());
            
            if (!finalScores.isEmpty()) {
                BigDecimal totalScore = finalScores.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setAverageScore(totalScore.divide(BigDecimal.valueOf(finalScores.size()), 2, RoundingMode.HALF_UP));
                
                long passCount = finalScores.stream()
                        .filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0)
                        .count();
                vo.setPassRate(BigDecimal.valueOf(passCount * 100.0 / finalScores.size())
                        .setScale(2, RoundingMode.HALF_UP));
                
                long excellentCount = finalScores.stream()
                        .filter(s -> s.compareTo(BigDecimal.valueOf(90)) >= 0)
                        .count();
                vo.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / finalScores.size())
                        .setScale(2, RoundingMode.HALF_UP));
            } else {
                vo.setAverageScore(BigDecimal.ZERO);
                vo.setPassRate(BigDecimal.ZERO);
                vo.setExcellentRate(BigDecimal.ZERO);
            }
        } else {
            vo.setAverageScore(BigDecimal.ZERO);
            vo.setPassRate(BigDecimal.ZERO);
            vo.setExcellentRate(BigDecimal.ZERO);
        }
        
        return vo;
    }
    
    @Override
    public List<CourseStatisticsVO> getCourseStatistics() {
        List<Course> courses = courseMapper.selectList(null);
        List<CourseStatisticsVO> result = new ArrayList<>();
        
        for (Course course : courses) {
            CourseStatisticsVO vo = getCourseStatisticsById(course.getCourseId());
            if (vo != null) {
                result.add(vo);
            }
        }
        
        return result;
    }
    
    @Override
    public List<CourseStatisticsVO> getCourseStatisticsByTeacher(Long teacherDbId) {
        // 查询该教师的所有教学班
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getTeacherDbId, teacherDbId);
        List<TeachingClass> teacherClasses = teachingClassMapper.selectList(tcWrapper);
        
        if (teacherClasses.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取该教师教的所有课程（去重）
        Set<Long> courseDbIds = teacherClasses.stream().map(TeachingClass::getCourseDbId).collect(Collectors.toSet());
        
        List<CourseStatisticsVO> result = new ArrayList<>();
        for (Long courseDbId : courseDbIds) {
            Course course = courseMapper.selectById(courseDbId);
            if (course == null) continue;
            
            CourseStatisticsVO vo = new CourseStatisticsVO();
            vo.setCourseId(course.getCourseId());
            vo.setCourseName(course.getCourseName());
            
            // 只统计该教师在该课程的教学班
            List<TeachingClass> courseTeacherClasses = teacherClasses.stream()
                    .filter(tc -> tc.getCourseDbId().equals(courseDbId))
                    .toList();
            
            long totalEnrollment = 0;
            List<BigDecimal> allScores = new ArrayList<>();
            
            for (TeachingClass tc : courseTeacherClasses) {
                LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
                enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, tc.getId());
                totalEnrollment += enrollmentMapper.selectCount(enrollmentWrapper);
                
                LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
                scoreWrapper.eq(Score::getTeachingClassDbId, tc.getId());
                List<Score> scores = scoreMapper.selectList(scoreWrapper);
                for (Score score : scores) {
                    if (score.getFinalScore() != null) {
                        allScores.add(score.getFinalScore());
                    }
                }
            }
            
            vo.setStudentCount(totalEnrollment);
            
            if (!allScores.isEmpty()) {
                BigDecimal sum = allScores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal max = allScores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                BigDecimal min = allScores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                long passCount = allScores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0).count();
                long excellentCount = allScores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(90)) >= 0).count();
                
                vo.setAverageScore(sum.divide(BigDecimal.valueOf(allScores.size()), 2, RoundingMode.HALF_UP));
                vo.setMaxScore(max);
                vo.setMinScore(min);
                vo.setPassCount(passCount);
                vo.setPassRate(BigDecimal.valueOf(passCount * 100.0 / allScores.size())
                        .setScale(2, RoundingMode.HALF_UP));
                vo.setExcellentCount(excellentCount);
                vo.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / allScores.size())
                        .setScale(2, RoundingMode.HALF_UP));
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    public List<ClassStatisticsVO> getClassStatistics() {
        List<TeachingClass> teachingClasses = teachingClassMapper.selectList(null);
        List<ClassStatisticsVO> result = new ArrayList<>();
        
        for (TeachingClass tc : teachingClasses) {
            ClassStatisticsVO vo = getClassStatisticsById(tc.getClassId());
            if (vo != null) {
                result.add(vo);
            }
        }
        
        return result;
    }
    
    @Override
    public List<ClassStatisticsVO> getClassStatisticsByTeacher(Long teacherDbId) {
        // 查询该教师的所有教学班
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getTeacherDbId, teacherDbId);
        List<TeachingClass> teacherClasses = teachingClassMapper.selectList(tcWrapper);
        
        List<ClassStatisticsVO> result = new ArrayList<>();
        for (TeachingClass tc : teacherClasses) {
            ClassStatisticsVO vo = getClassStatisticsById(tc.getClassId());
            if (vo != null) {
                result.add(vo);
            }
        }
        
        return result;
    }
    
    @Override
    public CourseStatisticsVO getCourseStatisticsById(String courseId) {
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getCourseId, courseId);
        Course course = courseMapper.selectOne(courseWrapper);
        if (course == null) {
            return null;
        }
        
        CourseStatisticsVO vo = new CourseStatisticsVO();
        vo.setCourseId(course.getCourseId());
        vo.setCourseName(course.getCourseName());
        
        // 查询该课程的所有教学班(需要先查询courseDbId)
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getCourseDbId, course.getId());
        List<TeachingClass> teachingClasses = teachingClassMapper.selectList(tcWrapper);
        
        // 统计选课人数和成绩
        long totalEnrollment = 0;
        List<BigDecimal> allScores = new ArrayList<>();
        
        for (TeachingClass tc : teachingClasses) {
            LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
            enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, tc.getId());
            totalEnrollment += enrollmentMapper.selectCount(enrollmentWrapper);
            
            LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
            scoreWrapper.eq(Score::getTeachingClassDbId, tc.getId());
            List<Score> scores = scoreMapper.selectList(scoreWrapper);
            for (Score score : scores) {
                if (score.getFinalScore() != null) {
                    allScores.add(score.getFinalScore());
                }
            }
        }
        
        vo.setStudentCount(totalEnrollment);
        
        if (!allScores.isEmpty()) {
            BigDecimal sum = allScores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal max = allScores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal min = allScores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            long passCount = allScores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0).count();
            long excellentCount = allScores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(90)) >= 0).count();
            
            vo.setAverageScore(sum.divide(BigDecimal.valueOf(allScores.size()), 2, RoundingMode.HALF_UP));
            vo.setMaxScore(max);
            vo.setMinScore(min);
            vo.setPassCount(passCount);
            vo.setPassRate(BigDecimal.valueOf(passCount * 100.0 / allScores.size())
                    .setScale(2, RoundingMode.HALF_UP));
            vo.setExcellentCount(excellentCount);
            vo.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / allScores.size())
                    .setScale(2, RoundingMode.HALF_UP));
        }
        
        return vo;
    }
    
    @Override
    public ClassStatisticsVO getClassStatisticsById(String classId) {
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass teachingClass = teachingClassMapper.selectOne(tcWrapper);
        if (teachingClass == null) {
            return null;
        }
        
        ClassStatisticsVO vo = new ClassStatisticsVO();
        vo.setClassName(classId); // ClassStatisticsVO有className字段
        
        // 查询课程信息
        Course course = courseMapper.selectById(teachingClass.getCourseDbId());
        if (course != null) {
            vo.setClassName(course.getCourseName() + " - " + classId);
        }
        
        // 统计选课人数
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        vo.setStudentCount(enrollmentMapper.selectCount(enrollmentWrapper));
        
        // 统计成绩
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
        List<Score> scores = scoreMapper.selectList(scoreWrapper);
        
        List<BigDecimal> scoreValues = scores.stream()
                .filter(s -> s.getFinalScore() != null)
                .map(Score::getFinalScore)
                .collect(Collectors.toList());
        
        if (!scoreValues.isEmpty()) {
            BigDecimal sum = scoreValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal max = scoreValues.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal min = scoreValues.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            long passCount = scoreValues.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0).count();
            long excellentCount = scoreValues.stream().filter(s -> s.compareTo(BigDecimal.valueOf(90)) >= 0).count();
            
            vo.setAverageScore(sum.divide(BigDecimal.valueOf(scoreValues.size()), 2, RoundingMode.HALF_UP));
            vo.setMaxScore(max);
            vo.setMinScore(min);
            vo.setPassRate(BigDecimal.valueOf(passCount * 100.0 / scoreValues.size())
                    .setScale(2, RoundingMode.HALF_UP));
            vo.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / scoreValues.size())
                    .setScale(2, RoundingMode.HALF_UP));
        }
        
        return vo;
    }
    
    @Override
    public List<Integer> getScoreDistribution() {
        List<Score> scores = scoreMapper.selectList(null);
        return calculateDistribution(scores);
    }
    
    @Override
    public List<Integer> getScoreDistributionByTeacher(Long teacherDbId) {
        // 查询该教师的所有教学班
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getTeacherDbId, teacherDbId);
        List<TeachingClass> teacherClasses = teachingClassMapper.selectList(tcWrapper);
        
        if (teacherClasses.isEmpty()) {
            return List.of(0, 0, 0, 0, 0);
        }
        
        List<Long> classDbIds = teacherClasses.stream().map(TeachingClass::getId).toList();
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.in(Score::getTeachingClassDbId, classDbIds);
        List<Score> scores = scoreMapper.selectList(scoreWrapper);
        return calculateDistribution(scores);
    }
    
    @Override
    public List<Integer> getClassScoreDistribution(String classId) {
        // 先查询TeachingClass获取数据库ID
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass tc = teachingClassMapper.selectOne(tcWrapper);
        if (tc == null) {
            return List.of(0, 0, 0, 0, 0);
        }
        
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getTeachingClassDbId, tc.getId());
        List<Score> scores = scoreMapper.selectList(wrapper);
        return calculateDistribution(scores);
    }
    
    private List<Integer> calculateDistribution(List<Score> scores) {
        // 分数段: <60, 60-69, 70-79, 80-89, 90-100
        int[] distribution = new int[5];
        
        for (Score score : scores) {
            if (score.getFinalScore() != null) {
                double s = score.getFinalScore().doubleValue();
                if (s < 60) distribution[0]++;
                else if (s < 70) distribution[1]++;
                else if (s < 80) distribution[2]++;
                else if (s < 90) distribution[3]++;
                else distribution[4]++;
            }
        }
        
        return Arrays.stream(distribution).boxed().collect(Collectors.toList());
    }
}
