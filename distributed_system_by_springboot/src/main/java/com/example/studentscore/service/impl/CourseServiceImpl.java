package com.example.studentscore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.common.ResultCode;
import com.example.studentscore.dto.CourseDTO;
import com.example.studentscore.entity.Course;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.CourseMapper;
import com.example.studentscore.mapper.TeachingClassMapper;
import com.example.studentscore.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final TeachingClassMapper teachingClassMapper;

    @Override
    public IPage<Course> pageCourses(Integer pageNum, Integer pageSize, String keyword) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Course::getCourseId, keyword)
                             .or()
                             .like(Course::getCourseName, keyword));
        }
        
        wrapper.orderByDesc(Course::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Course getByCourseId(String courseId) {
        return lambdaQuery().eq(Course::getCourseId, courseId).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCourse(CourseDTO dto) {
        // 检查课程编号是否已存在
        if (getByCourseId(dto.getCourseId()) != null) {
            throw new BusinessException(ResultCode.ALREADY_EXISTS, "课程编号已存在: " + dto.getCourseId());
        }
        
        Course course = new Course();
        BeanUtil.copyProperties(dto, course);
        course.setId(null); // 确保新增时ID为null，让数据库自动生成
        return save(course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourse(CourseDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "课程ID不能为空");
        }
        
        Course existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }
        
        // 如果修改了编号，检查新编号是否已存在
        if (!existing.getCourseId().equals(dto.getCourseId())) {
            if (getByCourseId(dto.getCourseId()) != null) {
                throw new BusinessException(ResultCode.ALREADY_EXISTS, "课程编号已存在: " + dto.getCourseId());
            }
        }
        
        BeanUtil.copyProperties(dto, existing);
        return updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddCourses(List<CourseDTO> dtoList) {
        List<Course> courses = dtoList.stream().map(dto -> {
            Course course = new Course();
            BeanUtil.copyProperties(dto, course);
            return course;
        }).collect(Collectors.toList());
        return saveBatch(courses);
    }

    /**
     * 删除课程时检查是否有关联教学班
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        Long courseId = Long.valueOf(id.toString());
        // 检查是否有关联的教学班
        long classCount = teachingClassMapper.selectCount(new LambdaQueryWrapper<TeachingClass>()
                .eq(TeachingClass::getCourseDbId, courseId));
        if (classCount > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该课程有关联的教学班，无法删除，请先删除相关教学班");
        }
        return super.removeById(id);
    }

    /**
     * 批量删除课程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> list) {
        for (Object id : list) {
            removeById((Serializable) id);
        }
        return true;
    }
}
