package com.example.studentscore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studentscore.entity.Course;
import com.example.studentscore.entity.Score;
import com.example.studentscore.entity.Student;
import com.example.studentscore.entity.Teacher;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.excel.CourseExcel;
import com.example.studentscore.excel.ExcelDataListener;
import com.example.studentscore.excel.ScoreExcel;
import com.example.studentscore.excel.StudentExcel;
import com.example.studentscore.excel.TeacherExcel;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.CourseMapper;
import com.example.studentscore.mapper.ScoreMapper;
import com.example.studentscore.mapper.StudentMapper;
import com.example.studentscore.mapper.TeacherMapper;
import com.example.studentscore.mapper.TeachingClassMapper;
import com.example.studentscore.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Excel服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final TeachingClassMapper teachingClassMapper;
    private final ScoreMapper scoreMapper;

    @Override
    public void exportStudents(HttpServletResponse response, String className, String grade) {
        try {
            // 查询学生数据
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            if (StrUtil.isNotBlank(className)) {
                queryWrapper.like(Student::getClassName, className);
            }
            if (StrUtil.isNotBlank(grade)) {
                queryWrapper.eq(Student::getGrade, grade);
            }
            List<Student> students = studentMapper.selectList(queryWrapper);

            // 转换为Excel对象
            List<StudentExcel> excelList = students.stream().map(student -> {
                StudentExcel excel = new StudentExcel();
                BeanUtil.copyProperties(student, excel);
                excel.setGender("MALE".equals(student.getGender()) ? "男" : "女");
                return excel;
            }).collect(Collectors.toList());

            // 设置响应头
            setExcelResponse(response, "学生信息");

            // 写入数据
            EasyExcel.write(response.getOutputStream(), StudentExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("学生信息")
                    .doWrite(excelList);
        } catch (IOException e) {
            log.error("导出学生数据失败", e);
            throw new BusinessException("导出学生数据失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importStudents(MultipartFile file) {
        AtomicInteger count = new AtomicInteger(0);
        try {
            ExcelDataListener<StudentExcel> listener = new ExcelDataListener<>(dataList -> {
                for (StudentExcel excel : dataList) {
                    // 检查学号是否已存在
                    Student existStudent = studentMapper.selectOne(
                            new LambdaQueryWrapper<Student>().eq(Student::getStudentId, excel.getStudentId()));
                    
                    Student student = new Student();
                    BeanUtil.copyProperties(excel, student);
                    student.setGender("男".equals(excel.getGender()) ? "MALE" : "FEMALE");
                    
                    if (existStudent != null) {
                        student.setId(existStudent.getId());
                        studentMapper.updateById(student);
                    } else {
                        studentMapper.insert(student);
                    }
                    count.incrementAndGet();
                }
            });

            EasyExcel.read(file.getInputStream(), StudentExcel.class, listener).sheet().doRead();
            return count.get();
        } catch (IOException e) {
            log.error("导入学生数据失败", e);
            throw new BusinessException("导入学生数据失败：" + e.getMessage());
        }
    }

    @Override
    public void downloadStudentTemplate(HttpServletResponse response) {
        try {
            setExcelResponse(response, "学生导入模板");

            // 示例数据
            StudentExcel example = new StudentExcel();
            example.setStudentId("2023010001");
            example.setName("张三");
            example.setGender("男");
            example.setClassName("计算机2301");
            example.setGrade("2023");
            example.setMajor("计算机科学与技术");
            example.setPhone("13800138000");
            example.setEmail("zhangsan@example.com");

            EasyExcel.write(response.getOutputStream(), StudentExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("学生导入模板")
                    .doWrite(Collections.singletonList(example));
        } catch (IOException e) {
            log.error("下载学生模板失败", e);
            throw new BusinessException("下载学生模板失败：" + e.getMessage());
        }
    }

    @Override
    public void exportScores(HttpServletResponse response, Long teachingClassId, String semester, Long studentDbId) {
        try {
            // 查询成绩数据并关联其他信息
            LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
            if (teachingClassId != null) {
                wrapper.eq(Score::getTeachingClassDbId, teachingClassId);
            }
            // 如果指定了学生ID（学生角色），则只查询该学生的成绩
            if (studentDbId != null) {
                wrapper.eq(Score::getStudentDbId, studentDbId);
            }
            List<Score> scores = scoreMapper.selectList(wrapper);

            // 获取关联数据
            Map<Long, Student> studentMap = studentMapper.selectList(null).stream()
                    .collect(Collectors.toMap(Student::getId, s -> s));
            Map<Long, TeachingClass> classMap = teachingClassMapper.selectList(null).stream()
                    .collect(Collectors.toMap(TeachingClass::getId, t -> t));
            Map<Long, Course> courseMap = courseMapper.selectList(null).stream()
                    .collect(Collectors.toMap(Course::getId, c -> c));
            Map<Long, Teacher> teacherMap = teacherMapper.selectList(null).stream()
                    .collect(Collectors.toMap(Teacher::getId, t -> t));

            // 转换为Excel对象 - 使用Set去重避免重复数据
            Set<String> processedKeys = new HashSet<>();
            List<ScoreExcel> excelList = scores.stream()
                    .filter(score -> {
                        // 使用学生ID+教学班ID作为唯一键去重
                        String key = score.getStudentDbId() + "_" + score.getTeachingClassDbId();
                        return processedKeys.add(key);
                    })
                    .map(score -> {
                ScoreExcel excel = new ScoreExcel();
                BeanUtil.copyProperties(score, excel);
                
                Student student = studentMap.get(score.getStudentDbId());
                if (student != null) {
                    excel.setStudentId(student.getStudentId());
                    excel.setStudentName(student.getName());
                }
                
                TeachingClass teachingClass = classMap.get(score.getTeachingClassDbId());
                if (teachingClass != null) {
                    excel.setClassId(teachingClass.getClassId());
                    excel.setSemester(teachingClass.getSemester());
                    
                    Course course = courseMap.get(teachingClass.getCourseDbId());
                    if (course != null) {
                        excel.setCourseName(course.getCourseName());
                    }
                    
                    Teacher teacher = teacherMap.get(teachingClass.getTeacherDbId());
                    if (teacher != null) {
                        excel.setTeacherName(teacher.getName());
                    }
                }
                
                return excel;
            }).collect(Collectors.toList());

            // 过滤学期
            if (StrUtil.isNotBlank(semester)) {
                excelList = excelList.stream()
                        .filter(e -> semester.equals(e.getSemester()))
                        .collect(Collectors.toList());
            }

            setExcelResponse(response, "成绩信息");

            EasyExcel.write(response.getOutputStream(), ScoreExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("成绩信息")
                    .doWrite(excelList);
        } catch (IOException e) {
            log.error("导出成绩数据失败", e);
            throw new BusinessException("导出成绩数据失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importScores(MultipartFile file) {
        AtomicInteger count = new AtomicInteger(0);
        try {
            // 预加载映射数据
            Map<String, Long> studentIdMap = studentMapper.selectList(null).stream()
                    .collect(Collectors.toMap(Student::getStudentId, Student::getId));
            Map<String, Long> classIdMap = teachingClassMapper.selectList(null).stream()
                    .collect(Collectors.toMap(TeachingClass::getClassId, TeachingClass::getId));

            ExcelDataListener<ScoreExcel> listener = new ExcelDataListener<>(dataList -> {
                for (ScoreExcel excel : dataList) {
                    Long studentDbId = studentIdMap.get(excel.getStudentId());
                    Long teachingClassDbId = classIdMap.get(excel.getClassId());
                    
                    if (studentDbId == null || teachingClassDbId == null) {
                        log.warn("学号 {} 或教学班号 {} 不存在，跳过", excel.getStudentId(), excel.getClassId());
                        continue;
                    }

                    // 检查成绩是否已存在
                    Score existScore = scoreMapper.selectOne(
                            new LambdaQueryWrapper<Score>()
                                    .eq(Score::getStudentDbId, studentDbId)
                                    .eq(Score::getTeachingClassDbId, teachingClassDbId));

                    Score score = new Score();
                    score.setStudentDbId(studentDbId);
                    score.setTeachingClassDbId(teachingClassDbId);
                    score.setRegularScore(excel.getRegularScore());
                    score.setMidtermScore(excel.getMidtermScore());
                    score.setExperimentScore(excel.getExperimentScore());
                    score.setFinalExamScore(excel.getFinalExamScore());
                    score.calculateFinalScore();

                    if (existScore != null) {
                        score.setId(existScore.getId());
                        scoreMapper.updateById(score);
                    } else {
                        scoreMapper.insert(score);
                    }
                    count.incrementAndGet();
                }
            });

            EasyExcel.read(file.getInputStream(), ScoreExcel.class, listener).sheet().doRead();
            return count.get();
        } catch (IOException e) {
            log.error("导入成绩数据失败", e);
            throw new BusinessException("导入成绩数据失败：" + e.getMessage());
        }
    }

    @Override
    public void downloadScoreTemplate(HttpServletResponse response) {
        try {
            setExcelResponse(response, "成绩导入模板");

            ScoreExcel example = new ScoreExcel();
            example.setStudentId("2023010001");
            example.setStudentName("张三（可不填）");
            example.setClassId("C001-01");
            example.setCourseName("高等数学（可不填）");
            example.setTeacherName("张明（可不填）");
            example.setSemester("2023-2024-1（可不填）");
            example.setRegularScore(new BigDecimal("85"));
            example.setMidtermScore(new BigDecimal("80"));
            example.setExperimentScore(new BigDecimal("88"));
            example.setFinalExamScore(new BigDecimal("82"));
            example.setFinalScore(new BigDecimal("83.20"));

            EasyExcel.write(response.getOutputStream(), ScoreExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("成绩导入模板")
                    .doWrite(Collections.singletonList(example));
        } catch (IOException e) {
            log.error("下载成绩模板失败", e);
            throw new BusinessException("下载成绩模板失败：" + e.getMessage());
        }
    }

    @Override
    public void exportTeachers(HttpServletResponse response) {
        try {
            List<Teacher> teachers = teacherMapper.selectList(null);

            List<TeacherExcel> excelList = teachers.stream().map(teacher -> {
                TeacherExcel excel = new TeacherExcel();
                BeanUtil.copyProperties(teacher, excel);
                return excel;
            }).collect(Collectors.toList());

            setExcelResponse(response, "教师信息");

            EasyExcel.write(response.getOutputStream(), TeacherExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("教师信息")
                    .doWrite(excelList);
        } catch (IOException e) {
            log.error("导出教师数据失败", e);
            throw new BusinessException("导出教师数据失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importTeachers(MultipartFile file) {
        AtomicInteger count = new AtomicInteger(0);
        try {
            ExcelDataListener<TeacherExcel> listener = new ExcelDataListener<>(dataList -> {
                for (TeacherExcel excel : dataList) {
                    Teacher existTeacher = teacherMapper.selectOne(
                            new LambdaQueryWrapper<Teacher>().eq(Teacher::getTeacherId, excel.getTeacherId()));

                    Teacher teacher = new Teacher();
                    BeanUtil.copyProperties(excel, teacher);

                    if (existTeacher != null) {
                        teacher.setId(existTeacher.getId());
                        teacherMapper.updateById(teacher);
                    } else {
                        teacherMapper.insert(teacher);
                    }
                    count.incrementAndGet();
                }
            });

            EasyExcel.read(file.getInputStream(), TeacherExcel.class, listener).sheet().doRead();
            return count.get();
        } catch (IOException e) {
            log.error("导入教师数据失败", e);
            throw new BusinessException("导入教师数据失败：" + e.getMessage());
        }
    }

    @Override
    public void downloadTeacherTemplate(HttpServletResponse response) {
        try {
            setExcelResponse(response, "教师导入模板");

            TeacherExcel example = new TeacherExcel();
            example.setTeacherId("T0001");
            example.setName("张明");
            example.setTitle("教授");
            example.setDepartment("计算机学院");
            example.setPhone("13800138001");
            example.setEmail("zhangming@example.com");

            EasyExcel.write(response.getOutputStream(), TeacherExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("教师导入模板")
                    .doWrite(Collections.singletonList(example));
        } catch (IOException e) {
            log.error("下载教师模板失败", e);
            throw new BusinessException("下载教师模板失败：" + e.getMessage());
        }
    }

    @Override
    public void exportCourses(HttpServletResponse response) {
        try {
            List<Course> courses = courseMapper.selectList(null);

            List<CourseExcel> excelList = courses.stream().map(course -> {
                CourseExcel excel = new CourseExcel();
                BeanUtil.copyProperties(course, excel);
                excel.setCourseType("REQUIRED".equals(course.getCourseType()) ? "必修" : "选修");
                return excel;
            }).collect(Collectors.toList());

            setExcelResponse(response, "课程信息");

            EasyExcel.write(response.getOutputStream(), CourseExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("课程信息")
                    .doWrite(excelList);
        } catch (IOException e) {
            log.error("导出课程数据失败", e);
            throw new BusinessException("导出课程数据失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importCourses(MultipartFile file) {
        AtomicInteger count = new AtomicInteger(0);
        try {
            ExcelDataListener<CourseExcel> listener = new ExcelDataListener<>(dataList -> {
                for (CourseExcel excel : dataList) {
                    Course existCourse = courseMapper.selectOne(
                            new LambdaQueryWrapper<Course>().eq(Course::getCourseId, excel.getCourseId()));

                    Course course = new Course();
                    BeanUtil.copyProperties(excel, course);
                    course.setCourseType("必修".equals(excel.getCourseType()) ? "REQUIRED" : "ELECTIVE");

                    if (existCourse != null) {
                        course.setId(existCourse.getId());
                        courseMapper.updateById(course);
                    } else {
                        courseMapper.insert(course);
                    }
                    count.incrementAndGet();
                }
            });

            EasyExcel.read(file.getInputStream(), CourseExcel.class, listener).sheet().doRead();
            return count.get();
        } catch (IOException e) {
            log.error("导入课程数据失败", e);
            throw new BusinessException("导入课程数据失败：" + e.getMessage());
        }
    }

    @Override
    public void downloadCourseTemplate(HttpServletResponse response) {
        try {
            setExcelResponse(response, "课程导入模板");

            CourseExcel example = new CourseExcel();
            example.setCourseId("C001");
            example.setCourseName("高等数学");
            example.setCredits(4);
            example.setCourseType("必修");
            example.setHours(64);
            example.setDescription("高等数学是工科专业的基础课程");

            EasyExcel.write(response.getOutputStream(), CourseExcel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("课程导入模板")
                    .doWrite(Collections.singletonList(example));
        } catch (IOException e) {
            log.error("下载课程模板失败", e);
            throw new BusinessException("下载课程模板失败：" + e.getMessage());
        }
    }

    /**
     * 设置Excel响应头
     */
    private void setExcelResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
    }
}
