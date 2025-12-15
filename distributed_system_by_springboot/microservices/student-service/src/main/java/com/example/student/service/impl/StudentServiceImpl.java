package com.example.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.StudentDTO;
import com.example.common.entity.Enrollment;
import com.example.common.entity.Score;
import com.example.common.entity.Student;
import com.example.common.entity.User;
import com.example.common.exception.BusinessException;
import com.example.common.query.StudentQuery;
import com.example.common.util.PasswordUtil;
import com.example.common.vo.StudentScoreDetailVO;
import com.example.student.feign.ScoreFeignClient;
import com.example.student.mapper.EnrollmentMapper;
import com.example.student.mapper.ScoreMapper;
import com.example.student.mapper.StudentMapper;
import com.example.student.mapper.UserMapper;
import com.example.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 学生服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    
    private final StudentMapper studentMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final ScoreMapper scoreMapper;
    private final UserMapper userMapper;
    private final ScoreFeignClient scoreFeignClient;
    
    @Override
    public IPage<Student> page(StudentQuery query) {
        Page<Student> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getStudentId())) {
            wrapper.like(Student::getStudentId, query.getStudentId());
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Student::getName, query.getName());
        }
        if (StringUtils.hasText(query.getMajor())) {
            wrapper.like(Student::getMajor, query.getMajor());
        }
        if (StringUtils.hasText(query.getClassName())) {
            wrapper.like(Student::getClassName, query.getClassName());
        }
        if (query.getGrade() != null) {
            wrapper.eq(Student::getGrade, query.getGrade());
        }
        if (StringUtils.hasText(query.getGender())) {
            wrapper.eq(Student::getGender, query.getGender());
        }
        
        wrapper.orderByDesc(Student::getCreateTime);
        
        return studentMapper.selectPage(page, wrapper);
    }
    
    @Override
    public List<Student> list() {
        return studentMapper.selectList(null);
    }
    
    @Override
    public Student getById(String id) {
        return studentMapper.selectById(id);
    }
    
    @Override
    public Student getByStudentId(String studentId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentId, studentId);
        return studentMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(StudentDTO dto) {
        // 检查学号是否已存在
        if (existsByStudentId(dto.getStudentId())) {
            throw new BusinessException("学号已存在");
        }
        
        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        
        int rows = studentMapper.insert(student);
        
        // 同步创建用户账号
        if (rows > 0) {
            createUserAccount(dto.getStudentId(), dto.getName());
        }
        
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(StudentDTO dto) {
        Student existing = studentMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 如果修改了学号，检查新学号是否已存在
        if (!existing.getStudentId().equals(dto.getStudentId())) {
            if (existsByStudentId(dto.getStudentId())) {
                throw new BusinessException("学号已存在");
            }
            // 更新用户账号的用户名
            updateUserAccount(existing.getStudentId(), dto.getStudentId());
        }
        
        BeanUtils.copyProperties(dto, existing);
        return studentMapper.updateById(existing) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 删除相关选课记录
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getStudentDbId, student.getId());
        enrollmentMapper.delete(enrollmentWrapper);
        
        // 删除相关成绩记录
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getStudentDbId, student.getId());
        scoreMapper.delete(scoreWrapper);
        
        // 删除用户账号
        deleteUserAccount(student.getStudentId());
        
        return studentMapper.deleteById(id) > 0;
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
    public boolean existsByStudentId(String studentId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentId, studentId);
        return studentMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public StudentScoreDetailVO getScoreDetail(String studentId) {
        Student student = getByStudentId(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        try {
            // 调用成绩服务获取学生成绩详情
            return scoreFeignClient.getStudentScoreDetail(studentId);
        } catch (Exception e) {
            log.error("获取学生成绩详情失败: {}", e.getMessage());
            // 返回基本信息
            StudentScoreDetailVO vo = new StudentScoreDetailVO();
            vo.setStudentId(student.getStudentId());
            vo.setStudentName(student.getName());
            vo.setMajor(student.getMajor());
            vo.setClassName(student.getClassName());
            vo.setGender(student.getGender());
            return vo;
        }
    }
    
    @Override
    public long count() {
        return studentMapper.selectCount(null);
    }
    
    /**
     * 创建用户账号
     */
    private void createUserAccount(String studentId, String name) {
        User user = new User();
        user.setUsername(studentId);
        user.setPassword(PasswordUtil.encryptPassword("123456"));
        user.setRealName(name);
        user.setRole("student");
        user.setStatus(1);
        userMapper.insert(user);
    }
    
    /**
     * 更新用户账号
     */
    private void updateUserAccount(String oldStudentId, String newStudentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, oldStudentId);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            user.setUsername(newStudentId);
            userMapper.updateById(user);
        }
    }
    
    /**
     * 删除用户账号
     */
    private void deleteUserAccount(String studentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, studentId);
        userMapper.delete(wrapper);
    }
}
