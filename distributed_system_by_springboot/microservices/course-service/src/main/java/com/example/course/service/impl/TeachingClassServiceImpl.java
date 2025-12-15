package com.example.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.TeachingClassDTO;
import com.example.common.entity.*;
import com.example.common.exception.BusinessException;
import com.example.common.query.TeachingClassQuery;
import com.example.common.vo.ClassStudentVO;
import com.example.common.vo.TeachingClassVO;
import com.example.course.mapper.*;
import com.example.course.service.TeachingClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 教学班服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeachingClassServiceImpl implements TeachingClassService {
    
    private final TeachingClassMapper teachingClassMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ScoreMapper scoreMapper;
    private final StudentMapper studentMapper;
    
    @Override
    public IPage<TeachingClassVO> page(TeachingClassQuery query) {
        Page<TeachingClass> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<TeachingClass> wrapper = buildQueryWrapper(query);
        wrapper.orderByDesc(TeachingClass::getCreateTime);
        
        IPage<TeachingClass> result = teachingClassMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<TeachingClassVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(convertToVOList(result.getRecords()));
        
        return voPage;
    }
    
    @Override
    public List<TeachingClassVO> list() {
        List<TeachingClass> list = teachingClassMapper.selectList(null);
        return convertToVOList(list);
    }
    
    @Override
    public TeachingClassVO getById(String id) {
        TeachingClass teachingClass = teachingClassMapper.selectById(id);
        return teachingClass != null ? convertToVO(teachingClass) : null;
    }
    
    @Override
    public TeachingClassVO getByClassId(String classId) {
        LambdaQueryWrapper<TeachingClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass teachingClass = teachingClassMapper.selectOne(wrapper);
        return teachingClass != null ? convertToVO(teachingClass) : null;
    }
    
    @Override
    public List<TeachingClassVO> getByTeacherId(String teacherId) {
        // 先查询教师数据库ID
        LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(Teacher::getTeacherId, teacherId);
        Teacher teacher = teacherMapper.selectOne(teacherWrapper);
        if (teacher == null) {
            return List.of();
        }
        
        LambdaQueryWrapper<TeachingClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingClass::getTeacherDbId, teacher.getId());
        List<TeachingClass> list = teachingClassMapper.selectList(wrapper);
        return convertToVOList(list);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TeachingClassDTO dto) {
        // 检查教学班编号是否已存在
        if (existsByClassId(dto.getClassId())) {
            throw new BusinessException("教学班编号已存在");
        }
        
        // DTO已经包含courseDbId和teacherDbId,直接验证是否存在
        Course course = courseMapper.selectById(dto.getCourseDbId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        Teacher teacher = teacherMapper.selectById(dto.getTeacherDbId());
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        TeachingClass teachingClass = new TeachingClass();
        BeanUtils.copyProperties(dto, teachingClass);
        
        return teachingClassMapper.insert(teachingClass) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TeachingClassDTO dto) {
        TeachingClass existing = teachingClassMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("教学班不存在");
        }
        
        // 如果修改了教学班编号，检查新编号是否已存在
        if (!existing.getClassId().equals(dto.getClassId())) {
            if (existsByClassId(dto.getClassId())) {
                throw new BusinessException("教学班编号已存在");
            }
        }
        
        BeanUtils.copyProperties(dto, existing);
        return teachingClassMapper.updateById(existing) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        TeachingClass teachingClass = teachingClassMapper.selectById(id);
        if (teachingClass == null) {
            throw new BusinessException("教学班不存在");
        }
        
        // 删除相关选课记录
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        enrollmentMapper.delete(enrollmentWrapper);
        
        // 删除相关成绩记录
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
        scoreMapper.delete(scoreWrapper);
        
        return teachingClassMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<String> ids) {
        for (String id : ids) {
            delete(id);
        }
        return true;
    }
    
    @Override
    public boolean existsByClassId(String classId) {
        LambdaQueryWrapper<TeachingClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingClass::getClassId, classId);
        return teachingClassMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public long count() {
        return teachingClassMapper.selectCount(null);
    }
    
    @Override
    public List<ClassStudentVO> getStudents(Long classDbId) {
        // 查询该教学班的所有选课记录
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, classDbId);
        List<Enrollment> enrollments = enrollmentMapper.selectList(enrollmentWrapper);
        
        List<ClassStudentVO> result = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            // 查询学生信息
            Student student = studentMapper.selectById(enrollment.getStudentDbId());
            if (student == null) continue;
            
            ClassStudentVO vo = new ClassStudentVO();
            vo.setId(student.getId());
            vo.setStudentId(student.getStudentId());
            vo.setName(student.getName());
            vo.setClassName(student.getClassName());
            vo.setMajor(student.getMajor());
            
            // 查询成绩
            LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
            scoreWrapper.eq(Score::getStudentDbId, student.getId());
            scoreWrapper.eq(Score::getTeachingClassDbId, classDbId);
            Score score = scoreMapper.selectOne(scoreWrapper);
            
            if (score != null) {
                ClassStudentVO.ScoreInfo scoreInfo = new ClassStudentVO.ScoreInfo();
                scoreInfo.setId(score.getId());
                scoreInfo.setRegularScore(score.getRegularScore());
                scoreInfo.setMidtermScore(score.getMidtermScore());
                scoreInfo.setExperimentScore(score.getExperimentScore());
                scoreInfo.setFinalExamScore(score.getFinalExamScore());
                scoreInfo.setFinalScore(score.getFinalScore());
                vo.setScore(scoreInfo);
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    private LambdaQueryWrapper<TeachingClass> buildQueryWrapper(TeachingClassQuery query) {
        LambdaQueryWrapper<TeachingClass> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getClassId())) {
            wrapper.like(TeachingClass::getClassId, query.getClassId());
        }
        if (StringUtils.hasText(query.getCourseId())) {
            // 先查询课程数据库ID
            LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.eq(Course::getCourseId, query.getCourseId());
            Course course = courseMapper.selectOne(courseWrapper);
            if (course != null) {
                wrapper.eq(TeachingClass::getCourseDbId, course.getId());
            } else {
                wrapper.eq(TeachingClass::getId, ""); // 课程不存在,返回空结果
            }
        }
        if (StringUtils.hasText(query.getTeacherId())) {
            // 先查询教师数据库ID
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getTeacherId, query.getTeacherId());
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher != null) {
                wrapper.eq(TeachingClass::getTeacherDbId, teacher.getId());
            } else {
                wrapper.eq(TeachingClass::getId, ""); // 教师不存在,返回空结果
            }
        }
        if (StringUtils.hasText(query.getSemester())) {
            wrapper.eq(TeachingClass::getSemester, query.getSemester());
        }
        if (StringUtils.hasText(query.getCourseName())) {
            // 需要先查询课程
            LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.like(Course::getCourseName, query.getCourseName());
            List<Course> courses = courseMapper.selectList(courseWrapper);
            if (!courses.isEmpty()) {
                List<Long> courseDbIds = courses.stream().map(Course::getId).toList();
                wrapper.in(TeachingClass::getCourseDbId, courseDbIds);
            } else {
                // 没有匹配的课程，返回空结果
                wrapper.eq(TeachingClass::getId, "");
            }
        }
        if (StringUtils.hasText(query.getTeacherName())) {
            // 需要先查询教师
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.like(Teacher::getName, query.getTeacherName());
            List<Teacher> teachers = teacherMapper.selectList(teacherWrapper);
            if (!teachers.isEmpty()) {
                List<Long> teacherDbIds = teachers.stream().map(Teacher::getId).toList();
                wrapper.in(TeachingClass::getTeacherDbId, teacherDbIds);
            } else {
                // 没有匹配的教师，返回空结果
                wrapper.eq(TeachingClass::getId, "");
            }
        }
        
        return wrapper;
    }
    
    private TeachingClassVO convertToVO(TeachingClass teachingClass) {
        TeachingClassVO vo = new TeachingClassVO();
        BeanUtils.copyProperties(teachingClass, vo);
        
        // 查询课程信息
        Course course = courseMapper.selectById(teachingClass.getCourseDbId());
        if (course != null) {
            vo.setCourseId(course.getCourseId());
            vo.setCourseName(course.getCourseName());
            vo.setCredits(course.getCredits().intValue());
        }
        
        // 查询教师信息
        Teacher teacher = teacherMapper.selectById(teachingClass.getTeacherDbId());
        if (teacher != null) {
            vo.setTeacherId(teacher.getTeacherId());
            vo.setTeacherName(teacher.getName());
        }
        
        // 查询选课人数(使用currentSize字段,不是enrollmentCount)
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        vo.setCurrentSize(enrollmentMapper.selectCount(enrollmentWrapper).intValue());
        
        return vo;
    }
    
    private List<TeachingClassVO> convertToVOList(List<TeachingClass> list) {
        List<TeachingClassVO> voList = new ArrayList<>();
        for (TeachingClass tc : list) {
            voList.add(convertToVO(tc));
        }
        return voList;
    }
}
