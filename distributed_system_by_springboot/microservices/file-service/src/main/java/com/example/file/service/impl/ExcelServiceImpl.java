package com.example.file.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.entity.*;
import com.example.common.exception.BusinessException;
import com.example.common.util.PasswordUtil;
import com.example.file.excel.*;
import com.example.file.mapper.*;
import com.example.file.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private final ScoreMapper scoreMapper;
    private final TeachingClassMapper teachingClassMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importStudents(MultipartFile file) {
        List<StudentExcel> dataList = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        try {
            EasyExcel.read(file.getInputStream(), StudentExcel.class, new ReadListener<StudentExcel>() {
                @Override
                public void invoke(StudentExcel data, AnalysisContext context) {
                    dataList.add(data);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).sheet().doRead();
            
            for (int i = 0; i < dataList.size(); i++) {
                StudentExcel data = dataList.get(i);
                try {
                    // 检查学号是否存在
                    LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Student::getStudentId, data.getStudentId());
                    if (studentMapper.selectCount(wrapper) > 0) {
                        errors.add("第" + (i + 2) + "行: 学号 " + data.getStudentId() + " 已存在");
                        failCount++;
                        continue;
                    }
                    
                    Student student = new Student();
                    student.setStudentId(data.getStudentId());
                    student.setName(data.getName());
                    student.setGender(data.getGender());
                    student.setMajor(data.getMajor());
                    student.setClassName(data.getClassName());
                    student.setGrade(String.valueOf(data.getGrade()));
                    student.setPhone(data.getPhone());
                    student.setEmail(data.getEmail());
                    studentMapper.insert(student);
                    
                    // 创建用户账号
                    User user = new User();
                    user.setUsername(data.getStudentId());
                    user.setPassword(PasswordUtil.encryptPassword("123456"));
                    user.setRealName(data.getName());
                    user.setRole("student");
                    user.setStatus(1);
                    userMapper.insert(user);
                    
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 2) + "行: " + e.getMessage());
                    failCount++;
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", dataList.size());
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("errors", errors);
        return result;
    }
    
    @Override
    public void exportStudents(HttpServletResponse response) {
        try {
            setExcelResponse(response, "学生数据");
            List<Student> students = studentMapper.selectList(null);
            List<StudentExcel> dataList = students.stream().map(s -> {
                StudentExcel excel = new StudentExcel();
                excel.setStudentId(s.getStudentId());
                excel.setName(s.getName());
                excel.setGender(s.getGender());
                excel.setMajor(s.getMajor());
                excel.setClassName(s.getClassName());
                excel.setGrade(s.getGrade() != null ? Integer.parseInt(s.getGrade()) : null);
                excel.setPhone(s.getPhone());
                excel.setEmail(s.getEmail());
                return excel;
            }).toList();
            EasyExcel.write(response.getOutputStream(), StudentExcel.class).sheet("学生数据").doWrite(dataList);
        } catch (IOException e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importTeachers(MultipartFile file) {
        List<TeacherExcel> dataList = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        try {
            EasyExcel.read(file.getInputStream(), TeacherExcel.class, new ReadListener<TeacherExcel>() {
                @Override
                public void invoke(TeacherExcel data, AnalysisContext context) {
                    dataList.add(data);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).sheet().doRead();
            
            for (int i = 0; i < dataList.size(); i++) {
                TeacherExcel data = dataList.get(i);
                try {
                    LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Teacher::getTeacherId, data.getTeacherId());
                    if (teacherMapper.selectCount(wrapper) > 0) {
                        errors.add("第" + (i + 2) + "行: 工号 " + data.getTeacherId() + " 已存在");
                        failCount++;
                        continue;
                    }
                    
                    Teacher teacher = new Teacher();
                    teacher.setTeacherId(data.getTeacherId());
                    teacher.setName(data.getName());
                    teacher.setDepartment(data.getDepartment());
                    teacher.setTitle(data.getTitle());
                    teacher.setPhone(data.getPhone());
                    teacher.setEmail(data.getEmail());
                    teacherMapper.insert(teacher);
                    
                    // 创建用户账号
                    User user = new User();
                    user.setUsername(data.getTeacherId());
                    user.setPassword(PasswordUtil.encryptPassword("123456"));
                    user.setRealName(data.getName());
                    user.setRole("teacher");
                    user.setStatus(1);
                    userMapper.insert(user);
                    
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 2) + "行: " + e.getMessage());
                    failCount++;
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", dataList.size());
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("errors", errors);
        return result;
    }
    
    @Override
    public void exportTeachers(HttpServletResponse response) {
        try {
            setExcelResponse(response, "教师数据");
            List<Teacher> teachers = teacherMapper.selectList(null);
            List<TeacherExcel> dataList = teachers.stream().map(t -> {
                TeacherExcel excel = new TeacherExcel();
                excel.setTeacherId(t.getTeacherId());
                excel.setName(t.getName());
                excel.setDepartment(t.getDepartment());
                excel.setTitle(t.getTitle());
                excel.setPhone(t.getPhone());
                excel.setEmail(t.getEmail());
                return excel;
            }).toList();
            EasyExcel.write(response.getOutputStream(), TeacherExcel.class).sheet("教师数据").doWrite(dataList);
        } catch (IOException e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importCourses(MultipartFile file) {
        List<CourseExcel> dataList = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        try {
            EasyExcel.read(file.getInputStream(), CourseExcel.class, new ReadListener<CourseExcel>() {
                @Override
                public void invoke(CourseExcel data, AnalysisContext context) {
                    dataList.add(data);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).sheet().doRead();
            
            for (int i = 0; i < dataList.size(); i++) {
                CourseExcel data = dataList.get(i);
                try {
                    LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Course::getCourseId, data.getCourseId());
                    if (courseMapper.selectCount(wrapper) > 0) {
                        errors.add("第" + (i + 2) + "行: 课程号 " + data.getCourseId() + " 已存在");
                        failCount++;
                        continue;
                    }
                    
                    Course course = new Course();
                    course.setCourseId(data.getCourseId());
                    course.setCourseName(data.getName());
                    course.setCredits(BigDecimal.valueOf(data.getCredits()));
                    course.setHours(data.getHours());
                    course.setCourseType(data.getType());
                    course.setDescription(data.getDescription());
                    courseMapper.insert(course);
                    
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 2) + "行: " + e.getMessage());
                    failCount++;
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", dataList.size());
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("errors", errors);
        return result;
    }
    
    @Override
    public void exportCourses(HttpServletResponse response) {
        try {
            setExcelResponse(response, "课程数据");
            List<Course> courses = courseMapper.selectList(null);
            List<CourseExcel> dataList = courses.stream().map(c -> {
                CourseExcel excel = new CourseExcel();
                excel.setCourseId(c.getCourseId());
                excel.setName(c.getCourseName());
                excel.setCredits(c.getCredits().doubleValue());
                excel.setHours(c.getHours());
                excel.setType(c.getCourseType());
                excel.setDescription(c.getDescription());
                return excel;
            }).toList();
            EasyExcel.write(response.getOutputStream(), CourseExcel.class).sheet("课程数据").doWrite(dataList);
        } catch (IOException e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importScores(MultipartFile file) {
        List<ScoreExcel> dataList = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();
        
        try {
            EasyExcel.read(file.getInputStream(), ScoreExcel.class, new ReadListener<ScoreExcel>() {
                @Override
                public void invoke(ScoreExcel data, AnalysisContext context) {
                    dataList.add(data);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).sheet().doRead();
            
            for (int i = 0; i < dataList.size(); i++) {
                ScoreExcel data = dataList.get(i);
                try {
                    // 根据学号查找学生
                    LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
                    studentWrapper.eq(Student::getStudentId, data.getStudentId());
                    Student student = studentMapper.selectOne(studentWrapper);
                    if (student == null) {
                        errors.add("第" + (i + 2) + "行: 学号 " + data.getStudentId() + " 不存在");
                        failCount++;
                        continue;
                    }
                    
                    // 根据教学班编号查找教学班
                    LambdaQueryWrapper<TeachingClass> classWrapper = new LambdaQueryWrapper<>();
                    classWrapper.eq(TeachingClass::getClassId, data.getClassId());
                    TeachingClass teachingClass = teachingClassMapper.selectOne(classWrapper);
                    if (teachingClass == null) {
                        errors.add("第" + (i + 2) + "行: 教学班编号 " + data.getClassId() + " 不存在");
                        failCount++;
                        continue;
                    }
                    
                    // 检查成绩是否已存在
                    LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Score::getStudentDbId, student.getId());
                    wrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
                    Score existing = scoreMapper.selectOne(wrapper);
                    
                    if (existing != null) {
                        // 更新成绩
                        existing.setFinalScore(BigDecimal.valueOf(data.getScore()));
                        existing.setRegularScore(BigDecimal.valueOf(data.getUsualScore()));
                        existing.setFinalExamScore(BigDecimal.valueOf(data.getExamScore()));
                        scoreMapper.updateById(existing);
                    } else {
                        // 新增成绩
                        Score score = new Score();
                        score.setStudentDbId(student.getId());
                        score.setTeachingClassDbId(teachingClass.getId());
                        score.setFinalScore(BigDecimal.valueOf(data.getScore()));
                        score.setRegularScore(BigDecimal.valueOf(data.getUsualScore()));
                        score.setFinalExamScore(BigDecimal.valueOf(data.getExamScore()));
                        scoreMapper.insert(score);
                    }
                    
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 2) + "行: " + e.getMessage());
                    failCount++;
                }
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败: " + e.getMessage());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", dataList.size());
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("errors", errors);
        return result;
    }
    
    @Override
    public void exportScores(String classId, Long studentDbId, Long teacherDbId, HttpServletResponse response) {
        try {
            setExcelResponse(response, "成绩数据");
            LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
            if (classId != null && !classId.isEmpty()) {
                wrapper.eq(Score::getTeachingClassDbId, classId);
            }
            // 学生只导出自己的成绩
            if (studentDbId != null) {
                wrapper.eq(Score::getStudentDbId, studentDbId);
            }
            // 教师只导出自己教学班的成绩
            if (teacherDbId != null) {
                LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
                tcWrapper.eq(TeachingClass::getTeacherDbId, teacherDbId);
                List<TeachingClass> teacherClasses = teachingClassMapper.selectList(tcWrapper);
                if (!teacherClasses.isEmpty()) {
                    List<Long> classDbIds = teacherClasses.stream().map(TeachingClass::getId).toList();
                    wrapper.in(Score::getTeachingClassDbId, classDbIds);
                } else {
                    // 教师没有教学班，返回空结果
                    wrapper.isNull(Score::getId);
                }
            }
            List<Score> scores = scoreMapper.selectList(wrapper);
            
            List<ScoreExcel> dataList = scores.stream().map(s -> {
                ScoreExcel excel = new ScoreExcel();
                
                // 查询学生信息
                Student student = studentMapper.selectById(s.getStudentDbId());
                if (student != null) {
                    excel.setStudentId(student.getStudentId());
                    excel.setStudentName(student.getName());
                }
                
                // 查询教学班信息
                TeachingClass teachingClass = teachingClassMapper.selectById(s.getTeachingClassDbId());
                if (teachingClass != null) {
                    excel.setClassId(teachingClass.getClassId());
                }
                
                excel.setScore(s.getFinalScore() != null ? s.getFinalScore().doubleValue() : null);
                excel.setUsualScore(s.getRegularScore() != null ? s.getRegularScore().doubleValue() : null);
                excel.setExamScore(s.getFinalExamScore() != null ? s.getFinalExamScore().doubleValue() : null);
                
                // 查询课程名称
                if (teachingClass != null) {
                    Course course = courseMapper.selectById(teachingClass.getCourseDbId());
                    if (course != null) {
                        excel.setCourseName(course.getCourseName());
                    }
                }
                
                return excel;
            }).toList();
            
            EasyExcel.write(response.getOutputStream(), ScoreExcel.class).sheet("成绩数据").doWrite(dataList);
        } catch (IOException e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }
    
    @Override
    public void downloadTemplate(String type, HttpServletResponse response) {
        try {
            switch (type) {
                case "student":
                    setExcelResponse(response, "学生导入模板");
                    EasyExcel.write(response.getOutputStream(), StudentExcel.class).sheet("学生数据").doWrite(new ArrayList<>());
                    break;
                case "teacher":
                    setExcelResponse(response, "教师导入模板");
                    EasyExcel.write(response.getOutputStream(), TeacherExcel.class).sheet("教师数据").doWrite(new ArrayList<>());
                    break;
                case "course":
                    setExcelResponse(response, "课程导入模板");
                    EasyExcel.write(response.getOutputStream(), CourseExcel.class).sheet("课程数据").doWrite(new ArrayList<>());
                    break;
                case "score":
                    setExcelResponse(response, "成绩导入模板");
                    EasyExcel.write(response.getOutputStream(), ScoreExcel.class).sheet("成绩数据").doWrite(new ArrayList<>());
                    break;
                default:
                    throw new BusinessException("不支持的模板类型");
            }
        } catch (IOException e) {
            throw new BusinessException("下载模板失败: " + e.getMessage());
        }
    }
    
    private void setExcelResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");
    }
}
