package model;

import java.util.*;

/**
 * 教师实体类
 */
public class Teacher {
    private String teacherId;     // 教师编号
    private String name;          // 姓名
    private List<TeachingClass> teachingClasses;  // 所教班级

    public Teacher(String teacherId, String name) {
        this.teacherId = teacherId;
        this.name = name;
        this.teachingClasses = new ArrayList<>();
    }

    public void addTeachingClass(TeachingClass teachingClass) {
        if (!teachingClasses.contains(teachingClass)) {
            teachingClasses.add(teachingClass);
        }
    }

    // Getters
    public String getTeacherId() { return teacherId; }
    public String getName() { return name; }
    public List<TeachingClass> getTeachingClasses() { return teachingClasses; }

    @Override
    public String toString() {
        return String.format("教师编号: %s, 姓名: %s", teacherId, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(teacherId, teacher.teacherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacherId);
    }
}
