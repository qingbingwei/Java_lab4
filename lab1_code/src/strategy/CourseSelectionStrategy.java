package strategy;

import model.*;
import java.util.*;

/**
 * 选课策略接口
 */
@FunctionalInterface
public interface CourseSelectionStrategy {
    /**
     * 为学生选择课程
     * @param student 学生
     * @param availableClasses 可用的教学班
     * @param minCourses 最少选课数量
     */
    void selectCourses(Student student, List<TeachingClass> availableClasses, int minCourses);
}
