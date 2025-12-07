package service;

import model.*;
import factory.DataFactory;
import strategy.*;
import java.util.*;

/**
 * 数据管理器 (单例模式)
 * 管理系统中的所有数据
 */
public class DataManager {
    private static DataManager instance;
    
    private List<Student> students;
    private List<Teacher> teachers;
    private List<Course> courses;
    private List<TeachingClass> teachingClasses;
    
    private boolean initialized = false;

    private DataManager() {
        students = new ArrayList<>();
        teachers = new ArrayList<>();
        courses = new ArrayList<>();
        teachingClasses = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化系统数据
     */
    public void initializeData() {
        if (initialized) {
            System.out.println("数据已经初始化过了！");
            return;
        }

        System.out.println("正在初始化数据...");
        
        // 生成教师
        teachers = DataFactory.generateTeachers(8);
        System.out.println("✓ 生成了 " + teachers.size() + " 名教师");
        
        // 生成课程
        courses = DataFactory.generateCourses(5);
        System.out.println("✓ 生成了 " + courses.size() + " 门课程");
        
        // 生成教学班
        teachingClasses = DataFactory.generateTeachingClasses(courses, teachers, "2024秋季学期");
        System.out.println("✓ 生成了 " + teachingClasses.size() + " 个教学班");
        
        // 生成学生
        students = DataFactory.generateStudents(120);
        System.out.println("✓ 生成了 " + students.size() + " 名学生");
        
        initialized = true;
        System.out.println("数据初始化完成！\n");
    }

    /**
     * 学生选课
     */
    public void enrollStudents() {
        if (!initialized) {
            System.out.println("请先初始化数据！");
            return;
        }

        System.out.println("正在进行学生选课...");
        CourseSelectionStrategy strategy = new RandomCourseSelectionStrategy();
        
        for (Student student : students) {
            strategy.selectCourses(student, teachingClasses, 3);
        }
        
        // 统计选课结果
        long totalEnrollments = students.stream()
                .mapToInt(s -> s.getEnrolledClasses().size())
                .sum();
        
        System.out.println("✓ 选课完成！总选课数: " + totalEnrollments);
        System.out.println("✓ 平均每人选课: " + (totalEnrollments / students.size()) + " 门\n");
        
        // 显示教学班人数
        System.out.println("各教学班人数统计:");
        for (TeachingClass tc : teachingClasses) {
            System.out.printf("  %s - %s (%s): %d人\n",
                    tc.getClassId(), tc.getCourse().getCourseName(),
                    tc.getTeacher().getName(), tc.getCurrentSize());
        }
        System.out.println();
    }

    // Getters
    public List<Student> getStudents() { return students; }
    public List<Teacher> getTeachers() { return teachers; }
    public List<Course> getCourses() { return courses; }
    public List<TeachingClass> getTeachingClasses() { return teachingClasses; }
    public boolean isInitialized() { return initialized; }
}
