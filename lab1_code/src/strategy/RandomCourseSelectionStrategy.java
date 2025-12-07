package strategy;

import model.*;
import java.util.*;

/**
 * 随机选课策略
 * 学生随机选择指定数量的课程
 */
public class RandomCourseSelectionStrategy implements CourseSelectionStrategy {
    private static final Random random = new Random();

    @Override
    public void selectCourses(Student student, List<TeachingClass> availableClasses, int minCourses) {
        // 按课程分组教学班
        Map<Course, List<TeachingClass>> courseClassMap = new HashMap<>();
        for (TeachingClass tc : availableClasses) {
            courseClassMap.computeIfAbsent(tc.getCourse(), k -> new ArrayList<>()).add(tc);
        }

        // 随机选择课程
        List<Course> courses = new ArrayList<>(courseClassMap.keySet());
        Collections.shuffle(courses);

        int selectedCount = 0;
        int maxCourses = Math.min(minCourses + random.nextInt(3), courses.size()); // 选3-5门课

        for (Course course : courses) {
            if (selectedCount >= maxCourses) break;

            List<TeachingClass> classesForCourse = courseClassMap.get(course);
            // 随机选择一个未满的教学班
            Collections.shuffle(classesForCourse);

            for (TeachingClass tc : classesForCourse) {
                if (!tc.isFull() && tc.addStudent(student)) {
                    student.enrollClass(tc);
                    selectedCount++;
                    break;
                }
            }
        }
    }
}
