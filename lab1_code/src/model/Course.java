package model;

import java.util.Objects;

/**
 * 课程实体类
 */
public class Course {
    private String courseId;      // 课程编号
    private String courseName;    // 课程名称
    private int credits;          // 学分

    public Course(String courseId, String courseName, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
    }

    // Getters
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }

    @Override
    public String toString() {
        return String.format("课程编号: %s, 课程名称: %s, 学分: %d", courseId, courseName, credits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
