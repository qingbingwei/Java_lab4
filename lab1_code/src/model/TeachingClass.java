package model;

import java.util.*;

/**
 * 教学班实体类
 */
public class TeachingClass {
    private String classId;           // 教学班号
    private Course course;            // 课程
    private Teacher teacher;          // 教师
    private String semester;          // 开课学期
    private List<Student> students;   // 学生列表
    private int capacity;             // 容量

    public TeachingClass(String classId, Course course, Teacher teacher, String semester, int capacity) {
        this.classId = classId;
        this.course = course;
        this.teacher = teacher;
        this.semester = semester;
        this.capacity = capacity;
        this.students = new ArrayList<>();
    }

    public boolean addStudent(Student student) {
        if (students.size() < capacity && !students.contains(student)) {
            students.add(student);
            return true;
        }
        return false;
    }

    public int getCurrentSize() {
        return students.size();
    }

    public boolean isFull() {
        return students.size() >= capacity;
    }

    // Getters
    public String getClassId() { return classId; }
    public Course getCourse() { return course; }
    public Teacher getTeacher() { return teacher; }
    public String getSemester() { return semester; }
    public List<Student> getStudents() { return students; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return String.format("教学班号: %s, 课程: %s, 教师: %s, 学期: %s, 人数: %d/%d",
                classId, course.getCourseName(), teacher.getName(), semester, students.size(), capacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeachingClass that = (TeachingClass) o;
        return Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classId);
    }
}
