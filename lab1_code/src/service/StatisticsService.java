package service;

import model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务类
 * 提供各种成绩统计功能
 */
public class StatisticsService {
    
    /**
     * 成绩分数段枚举
     */
    public enum ScoreRange {
        EXCELLENT("优秀", 90, 100),
        GOOD("良好", 80, 89),
        MEDIUM("中等", 70, 79),
        PASS("及格", 60, 69),
        FAIL("不及格", 0, 59);

        private final String name;
        private final int min;
        private final int max;

        ScoreRange(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }

        public boolean contains(double score) {
            return score >= min && score <= max;
        }

        public String getName() { return name; }
    }

    /**
     * 统计教学班的成绩分布
     */
    public Map<ScoreRange, Long> getScoreDistribution(TeachingClass teachingClass) {
        Map<ScoreRange, Long> distribution = new LinkedHashMap<>();
        
        for (ScoreRange range : ScoreRange.values()) {
            long count = teachingClass.getStudents().stream()
                    .map(s -> s.getScore(teachingClass))
                    .filter(Objects::nonNull)
                    .filter(Score::isComplete)
                    .map(Score::getFinalScore)
                    .filter(range::contains)
                    .count();
            distribution.put(range, count);
        }
        
        return distribution;
    }

    /**
     * 统计所有学生总成绩的分布
     */
    public Map<ScoreRange, Long> getTotalScoreDistribution(List<Student> students) {
        Map<ScoreRange, Long> distribution = new LinkedHashMap<>();
        
        for (ScoreRange range : ScoreRange.values()) {
            long count = students.stream()
                    .map(Student::getAverageScore)
                    .filter(score -> score > 0)
                    .filter(range::contains)
                    .count();
            distribution.put(range, count);
        }
        
        return distribution;
    }

    /**
     * 按学号对教学班学生排序
     */
    public List<Student> sortByStudentId(TeachingClass teachingClass) {
        return teachingClass.getStudents().stream()
                .sorted(Comparator.comparing(Student::getStudentId))
                .collect(Collectors.toList());
    }

    /**
     * 按成绩对教学班学生排序
     */
    public List<Student> sortByScore(TeachingClass teachingClass, boolean ascending) {
        Comparator<Student> comparator = Comparator.comparing(
                s -> s.getScore(teachingClass) != null ? 
                     s.getScore(teachingClass).getFinalScore() : 0.0
        );
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return teachingClass.getStudents().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 按总成绩对所有学生排序
     */
    public List<Student> sortByTotalScore(List<Student> students, boolean ascending) {
        Comparator<Student> comparator = Comparator.comparing(Student::getAverageScore);
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return students.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * 查询学生的所有成绩
     */
    public Student findStudent(List<Student> students, String searchKey) {
        return students.stream()
                .filter(s -> s.getStudentId().equals(searchKey) || s.getName().equals(searchKey))
                .findFirst()
                .orElse(null);
    }
}
