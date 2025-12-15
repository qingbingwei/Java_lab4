package com.example.teacher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.TeacherDTO;
import com.example.common.entity.Teacher;
import com.example.common.entity.TeachingClass;
import com.example.common.entity.User;
import com.example.common.exception.BusinessException;
import com.example.common.util.PasswordUtil;
import com.example.teacher.mapper.TeacherMapper;
import com.example.teacher.mapper.TeachingClassMapper;
import com.example.teacher.mapper.UserMapper;
import com.example.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 教师服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    
    private final TeacherMapper teacherMapper;
    private final TeachingClassMapper teachingClassMapper;
    private final UserMapper userMapper;
    
    @Override
    public IPage<Teacher> page(Page<Teacher> page, String teacherId, String name, String department) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(teacherId)) {
            wrapper.like(Teacher::getTeacherId, teacherId);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Teacher::getName, name);
        }
        if (StringUtils.hasText(department)) {
            wrapper.like(Teacher::getDepartment, department);
        }
        
        wrapper.orderByDesc(Teacher::getCreateTime);
        
        return teacherMapper.selectPage(page, wrapper);
    }
    
    @Override
    public List<Teacher> list() {
        return teacherMapper.selectList(null);
    }
    
    @Override
    public Teacher getById(String id) {
        return teacherMapper.selectById(id);
    }
    
    @Override
    public Teacher getByTeacherId(String teacherId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherId, teacherId);
        return teacherMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TeacherDTO dto) {
        // 检查工号是否已存在
        if (existsByTeacherId(dto.getTeacherId())) {
            throw new BusinessException("工号已存在");
        }
        
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(dto, teacher);
        
        int rows = teacherMapper.insert(teacher);
        
        // 同步创建用户账号
        if (rows > 0) {
            createUserAccount(dto.getTeacherId(), dto.getName());
        }
        
        return rows > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TeacherDTO dto) {
        Teacher existing = teacherMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 如果修改了工号，检查新工号是否已存在
        if (!existing.getTeacherId().equals(dto.getTeacherId())) {
            if (existsByTeacherId(dto.getTeacherId())) {
                throw new BusinessException("工号已存在");
            }
            // 更新用户账号的用户名
            updateUserAccount(existing.getTeacherId(), dto.getTeacherId());
        }
        
        BeanUtils.copyProperties(dto, existing);
        return teacherMapper.updateById(existing) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 检查是否有关联的教学班
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getTeacherDbId, teacher.getId());
        Long tcCount = teachingClassMapper.selectCount(tcWrapper);
        if (tcCount > 0) {
            throw new BusinessException("该教师有关联的教学班，无法删除");
        }
        
        // 删除用户账号
        deleteUserAccount(teacher.getTeacherId());
        
        return teacherMapper.deleteById(id) > 0;
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
    public boolean existsByTeacherId(String teacherId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherId, teacherId);
        return teacherMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public long count() {
        return teacherMapper.selectCount(null);
    }
    
    /**
     * 创建用户账号
     */
    private void createUserAccount(String teacherId, String name) {
        User user = new User();
        user.setUsername(teacherId);
        user.setPassword(PasswordUtil.encryptPassword("123456"));
        user.setRealName(name);
        user.setRole("teacher");
        user.setStatus(1);
        userMapper.insert(user);
    }
    
    /**
     * 更新用户账号
     */
    private void updateUserAccount(String oldTeacherId, String newTeacherId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, oldTeacherId);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            user.setUsername(newTeacherId);
            userMapper.updateById(user);
        }
    }
    
    /**
     * 删除用户账号
     */
    private void deleteUserAccount(String teacherId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, teacherId);
        userMapper.delete(wrapper);
    }
}
