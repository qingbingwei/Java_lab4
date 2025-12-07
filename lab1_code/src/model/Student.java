package model;

import java.util.*;

/**
 * 学生实体类
 * 包含学生的基本信息和选课信息
 */
public class Student {
    private String studentId;      // 学号
    private String name;           // 姓名
    private Gender gender;         // 性别
    private List<TeachingClass> enrolledClasses;  // 已选课程
    private Map<TeachingClass, Score> scores;     // 成绩记录

    public Student(String studentId, String name, Gender gender) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.enrolledClasses = new ArrayList<>();
        this.scores = new HashMap<>();
    }

    public void enrollClass(TeachingClass teachingClass) {
        if (!enrolledClasses.contains(teachingClass)) {
            enrolledClasses.add(teachingClass);
        }
    }

    public void setScore(TeachingClass teachingClass, Score score) {
        scores.put(teachingClass, score);
    }

    public Score getScore(TeachingClass teachingClass) {
        return scores.get(teachingClass);
    }

    public double getTotalScore() {
        return scores.values().stream()
                .mapToDouble(Score::getFinalScore)
                .sum();
    }

    public double getAverageScore() {
        if (scores.isEmpty()) return 0;
        return getTotalScore() / scores.size();
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public Gender getGender() { return gender; }
    public List<TeachingClass> getEnrolledClasses() { return enrolledClasses; }
    public Map<TeachingClass, Score> getScores() { return scores; }

    @Override
    public String toString() {
        return String.format("学号: %s, 姓名: %s, 性别: %s", studentId, name, gender.getDescription());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}
