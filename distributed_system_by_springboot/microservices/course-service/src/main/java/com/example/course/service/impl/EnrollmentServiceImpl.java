package com.example.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.EnrollmentDTO;
import com.example.common.entity.*;
import com.example.common.exception.BusinessException;
import com.example.common.vo.EnrollmentVO;
import com.example.course.mapper.*;
import com.example.course.service.EnrollmentService;
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
 * 选课服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    
    private final EnrollmentMapper enrollmentMapper;
    private final StudentMapper studentMapper;
    private final TeachingClassMapper teachingClassMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final ScoreMapper scoreMapper;
    
    @Override
    public IPage<EnrollmentVO> page(Page<Enrollment> page, String studentId, String classId) {
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(studentId)) {
            // 查询学生数据库ID
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.like(Student::getStudentId, studentId);
            List<Student> students = studentMapper.selectList(studentWrapper);
            if (!students.isEmpty()) {
                List<Long> studentDbIds = students.stream().map(Student::getId).toList();
                wrapper.in(Enrollment::getStudentDbId, studentDbIds);
            } else {
                wrapper.isNull(Enrollment::getId);
            }
        }
        if (StringUtils.hasText(classId)) {
            // 查询教学班ID
            LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
            tcWrapper.like(TeachingClass::getClassId, classId);
            List<TeachingClass> tcs = teachingClassMapper.selectList(tcWrapper);
            if (!tcs.isEmpty()) {
                List<Long> classDbIds = tcs.stream().map(TeachingClass::getId).toList();
                wrapper.in(Enrollment::getTeachingClassDbId, classDbIds);
            } else {
                wrapper.isNull(Enrollment::getId);
            }
        }
        
        wrapper.orderByDesc(Enrollment::getCreateTime);
        
        IPage<Enrollment> result = enrollmentMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<EnrollmentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(convertToVOList(result.getRecords()));
        
        return voPage;
    }
    
    @Override
    public List<EnrollmentVO> getByStudentId(String studentId) {
        // 查询学生数据库ID
        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(Student::getStudentId, studentId);
        Student student = studentMapper.selectOne(studentWrapper);
        if (student == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentDbId, student.getId());
        List<Enrollment> list = enrollmentMapper.selectList(wrapper);
        return convertToVOList(list);
    }
    
    @Override
    public List<EnrollmentVO> getByClassId(String classId) {
        // 查询教学班ID
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass teachingClass = teachingClassMapper.selectOne(tcWrapper);
        if (teachingClass == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        List<Enrollment> list = enrollmentMapper.selectList(wrapper);
        return convertToVOList(list);
    }
    
    @Override
    public EnrollmentVO getById(String id) {
        Enrollment enrollment = enrollmentMapper.selectById(id);
        return enrollment != null ? convertToVO(enrollment) : null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(EnrollmentDTO dto) {
        // 检查是否已选课
        LambdaQueryWrapper<Enrollment> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Enrollment::getStudentDbId, dto.getStudentDbId());
        checkWrapper.eq(Enrollment::getTeachingClassDbId, dto.getTeachingClassDbId());
        if (enrollmentMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessException("该学生已选此教学班");
        }
        
        // 检查教学班容量
        TeachingClass teachingClass = teachingClassMapper.selectById(dto.getTeachingClassDbId());
        if (teachingClass == null) {
            throw new BusinessException("教学班不存在");
        }
        
        LambdaQueryWrapper<Enrollment> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Enrollment::getTeachingClassDbId, dto.getTeachingClassDbId());
        long currentCount = enrollmentMapper.selectCount(countWrapper);
        if (teachingClass.getCapacity() != null && currentCount >= teachingClass.getCapacity()) {
            throw new BusinessException("教学班已满");
        }
        
        Enrollment enrollment = new Enrollment();
        BeanUtils.copyProperties(dto, enrollment);
        
        return enrollmentMapper.insert(enrollment) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<EnrollmentDTO> dtos) {
        for (EnrollmentDTO dto : dtos) {
            save(dto);
        }
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        Enrollment enrollment = enrollmentMapper.selectById(id);
        if (enrollment == null) {
            throw new BusinessException("选课记录不存在");
        }
        
        // 删除相关成绩记录
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getStudentDbId, enrollment.getStudentDbId());
        scoreWrapper.eq(Score::getTeachingClassDbId, enrollment.getTeachingClassDbId());
        scoreMapper.delete(scoreWrapper);
        
        return enrollmentMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(String studentId, String classId) {
        // 查询学生和教学班ID
        Student student = studentMapper.selectOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentId, studentId));
        TeachingClass teachingClass = teachingClassMapper.selectOne(new LambdaQueryWrapper<TeachingClass>().eq(TeachingClass::getClassId, classId));
        if (student == null || teachingClass == null) {
            throw new BusinessException("选课记录不存在");
        }
        
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentDbId, student.getId());
        wrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        Enrollment enrollment = enrollmentMapper.selectOne(wrapper);
        
        if (enrollment == null) {
            throw new BusinessException("选课记录不存在");
        }
        
        // 删除相关成绩记录
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getStudentDbId, student.getId());
        scoreWrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
        scoreMapper.delete(scoreWrapper);
        
        return enrollmentMapper.delete(wrapper) > 0;
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
    public boolean exists(String studentId, String classId) {
        Student student = studentMapper.selectOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentId, studentId));
        TeachingClass teachingClass = teachingClassMapper.selectOne(new LambdaQueryWrapper<TeachingClass>().eq(TeachingClass::getClassId, classId));
        if (student == null || teachingClass == null) {
            return false;
        }
        
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getStudentDbId, student.getId());
        wrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        return enrollmentMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public long count() {
        return enrollmentMapper.selectCount(null);
    }
    
    @Override
    public long countByClassId(String classId) {
        TeachingClass teachingClass = teachingClassMapper.selectOne(new LambdaQueryWrapper<TeachingClass>().eq(TeachingClass::getClassId, classId));
        if (teachingClass == null) {
            return 0;
        }
        
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Enrollment::getTeachingClassDbId, teachingClass.getId());
        return enrollmentMapper.selectCount(wrapper);
    }
    
    private EnrollmentVO convertToVO(Enrollment enrollment) {
        EnrollmentVO vo = new EnrollmentVO();
        BeanUtils.copyProperties(enrollment, vo);
        
        // 查询学生信息
        Student student = studentMapper.selectById(enrollment.getStudentDbId());
        if (student != null) {
            vo.setStudentId(student.getStudentId());
            vo.setStudentName(student.getName());
        }
        
        // 查询教学班信息
        TeachingClass teachingClass = teachingClassMapper.selectById(enrollment.getTeachingClassDbId());
        if (teachingClass != null) {
            vo.setClassId(teachingClass.getClassId());
            vo.setClassName(teachingClass.getClassId());
            vo.setSemester(teachingClass.getSemester());
            
            // 查询课程信息
            Course course = courseMapper.selectById(teachingClass.getCourseDbId());
            if (course != null) {
                vo.setCourseCode(course.getCourseId());
                vo.setCourseName(course.getCourseName());
                vo.setCourseType(course.getCourseType());
                vo.setCredit(course.getCredits());
            }
            
            // 查询教师信息
            Teacher teacher = teacherMapper.selectById(teachingClass.getTeacherDbId());
            if (teacher != null) {
                vo.setTeacherId(teacher.getTeacherId());
                vo.setTeacherName(teacher.getName());
            }
        }
        
        return vo;
    }
    
    private List<EnrollmentVO> convertToVOList(List<Enrollment> list) {
        List<EnrollmentVO> voList = new ArrayList<>();
        for (Enrollment enrollment : list) {
            voList.add(convertToVO(enrollment));
        }
        return voList;
    }
}
