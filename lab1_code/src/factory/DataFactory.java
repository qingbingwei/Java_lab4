package factory;

import model.*;
import java.util.*;

/**
 * 数据工厂类
 * 使用工厂模式生成各种数据对象
 */
public class DataFactory {
    private static final Random random = new Random();
    
    // 姓氏库
    private static final String[] SURNAMES = {
        "王", "李", "张", "刘", "陈", "杨", "黄", "赵", "吴", "周",
        "徐", "孙", "马", "朱", "胡", "郭", "何", "高", "林", "罗"
    };
    
    // 名字库
    private static final String[] GIVEN_NAMES = {
        "伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋",
        "勇", "艳", "杰", "涛", "明", "超", "秀英", "建华", "国强", "志伟"
    };
    
    // 课程名称库
    private static final String[] COURSE_NAMES = {
        "数据结构", "计算机网络", "操作系统", "数据库原理",
        "软件工程", "编译原理", "人工智能", "计算机组成原理"
    };

    /**
     * 生成学生
     */
    public static List<Student> generateStudents(int count) {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String studentId = String.format("2024%04d", i + 1);
            String name = SURNAMES[random.nextInt(SURNAMES.length)] + 
                         GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
            Gender gender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
            students.add(new Student(studentId, name, gender));
        }
        return students;
    }

    /**
     * 生成教师
     */
    public static List<Teacher> generateTeachers(int count) {
        List<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String teacherId = String.format("T%04d", i + 1);
            String name = SURNAMES[random.nextInt(SURNAMES.length)] + 
                         GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
            teachers.add(new Teacher(teacherId, name));
        }
        return teachers;
    }

    /**
     * 生成课程
     */
    public static List<Course> generateCourses(int count) {
        List<Course> courses = new ArrayList<>();
        count = Math.min(count, COURSE_NAMES.length);
        for (int i = 0; i < count; i++) {
            String courseId = String.format("C%03d", i + 1);
            String courseName = COURSE_NAMES[i];
            int credits = 3 + random.nextInt(2); // 3-4学分
            courses.add(new Course(courseId, courseName, credits));
        }
        return courses;
    }

    /**
     * 生成教学班
     * 每门课至少2个教师上课
     */
    public static List<TeachingClass> generateTeachingClasses(
            List<Course> courses, List<Teacher> teachers, String semester) {
        List<TeachingClass> teachingClasses = new ArrayList<>();
        int classCounter = 1;

        for (Course course : courses) {
            // 每门课至少2个教学班(2个教师)
            int classesPerCourse = 2 + random.nextInt(2); // 2-3个班
            
            // 随机选择教师
            List<Teacher> shuffledTeachers = new ArrayList<>(teachers);
            Collections.shuffle(shuffledTeachers);

            for (int i = 0; i < classesPerCourse && i < shuffledTeachers.size(); i++) {
                String classId = String.format("TC%04d", classCounter++);
                Teacher teacher = shuffledTeachers.get(i);
                int capacity = 30 + random.nextInt(21); // 30-50人容量

                TeachingClass tc = new TeachingClass(classId, course, teacher, semester, capacity);
                teachingClasses.add(tc);
                teacher.addTeachingClass(tc);
            }
        }

        return teachingClasses;
    }
}
