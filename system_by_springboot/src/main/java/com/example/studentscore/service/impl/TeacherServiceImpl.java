package com.example.studentscore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.common.ResultCode;
import com.example.studentscore.dto.TeacherDTO;
import com.example.studentscore.entity.Teacher;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.entity.User;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.TeacherMapper;
import com.example.studentscore.mapper.TeachingClassMapper;
import com.example.studentscore.mapper.UserMapper;
import com.example.studentscore.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师服务实现类
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private final TeachingClassMapper teachingClassMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<Teacher> pageTeachers(Integer pageNum, Integer pageSize, String keyword) {
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Teacher::getTeacherId, keyword)
                             .or()
                             .like(Teacher::getName, keyword)
                             .or()
                             .like(Teacher::getDepartment, keyword));
        }
        
        wrapper.orderByDesc(Teacher::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Teacher getByTeacherId(String teacherId) {
        return lambdaQuery().eq(Teacher::getTeacherId, teacherId).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeacher(TeacherDTO dto) {
        // 检查教师编号是否已存在
        if (getByTeacherId(dto.getTeacherId()) != null) {
            throw new BusinessException(ResultCode.ALREADY_EXISTS, "教师编号已存在: " + dto.getTeacherId());
        }
        
        Teacher teacher = new Teacher();
        BeanUtil.copyProperties(dto, teacher);
        return save(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeacher(TeacherDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "教师ID不能为空");
        }
        
        Teacher existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "教师不存在");
        }
        
        // 如果修改了编号，检查新编号是否已存在
        if (!existing.getTeacherId().equals(dto.getTeacherId())) {
            if (getByTeacherId(dto.getTeacherId()) != null) {
                throw new BusinessException(ResultCode.ALREADY_EXISTS, "教师编号已存在: " + dto.getTeacherId());
            }
        }
        
        BeanUtil.copyProperties(dto, existing);
        return updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddTeachers(List<TeacherDTO> dtoList) {
        List<Teacher> teachers = dtoList.stream().map(dto -> {
            Teacher teacher = new Teacher();
            BeanUtil.copyProperties(dto, teacher);
            return teacher;
        }).collect(Collectors.toList());
        return saveBatch(teachers);
    }

    /**
     * 删除教师时级联删除关联数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        Long teacherId = Long.valueOf(id.toString());
        // 检查是否有关联的教学班
        long classCount = teachingClassMapper.selectCount(new LambdaQueryWrapper<TeachingClass>()
                .eq(TeachingClass::getTeacherDbId, teacherId));
        if (classCount > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该教师有关联的教学班，无法删除，请先移除教学班关联");
        }
        // 删除关联的用户账号
        userMapper.delete(new LambdaQueryWrapper<User>()
                .eq(User::getRefId, teacherId)
                .eq(User::getRole, "TEACHER"));
        // 删除教师本身
        return super.removeById(id);
    }

    /**
     * 批量删除教师时级联删除关联数据
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
