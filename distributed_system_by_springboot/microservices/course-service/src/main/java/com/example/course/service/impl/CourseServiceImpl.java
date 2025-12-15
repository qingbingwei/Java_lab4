package com.example.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.CourseDTO;
import com.example.common.entity.Course;
import com.example.common.entity.TeachingClass;
import com.example.common.exception.BusinessException;
import com.example.course.mapper.CourseMapper;
import com.example.course.mapper.TeachingClassMapper;
import com.example.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 课程服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    
    private final CourseMapper courseMapper;
    private final TeachingClassMapper teachingClassMapper;
    
    @Override
    public IPage<Course> page(Page<Course> page, String courseId, String name, String type) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(courseId)) {
            wrapper.like(Course::getCourseId, courseId);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Course::getCourseName, name);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Course::getCourseType, type);
        }
        
        wrapper.orderByDesc(Course::getCreateTime);
        
        return courseMapper.selectPage(page, wrapper);
    }
    
    @Override
    public List<Course> list() {
        return courseMapper.selectList(null);
    }
    
    @Override
    public Course getById(String id) {
        return courseMapper.selectById(id);
    }
    
    @Override
    public Course getByCourseId(String courseId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseId, courseId);
        return courseMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CourseDTO dto) {
        // 检查课程号是否已存在
        if (existsByCourseId(dto.getCourseId())) {
            throw new BusinessException("课程号已存在");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(dto, course);
        
        return courseMapper.insert(course) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CourseDTO dto) {
        Course existing = courseMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 如果修改了课程号，检查新课程号是否已存在
        if (!existing.getCourseId().equals(dto.getCourseId())) {
            if (existsByCourseId(dto.getCourseId())) {
                throw new BusinessException("课程号已存在");
            }
        }
        
        BeanUtils.copyProperties(dto, existing);
        return courseMapper.updateById(existing) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查是否有关联的教学班
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getCourseDbId, course.getId());
        Long tcCount = teachingClassMapper.selectCount(tcWrapper);
        if (tcCount > 0) {
            throw new BusinessException("该课程有关联的教学班，无法删除");
        }
        
        return courseMapper.deleteById(id) > 0;
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
    public boolean existsByCourseId(String courseId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseId, courseId);
        return courseMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public long count() {
        return courseMapper.selectCount(null);
    }
}
