package com.example.studentscore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.common.ResultCode;
import com.example.studentscore.dto.EnrollmentDTO;
import com.example.studentscore.entity.Enrollment;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.EnrollmentMapper;
import com.example.studentscore.service.EnrollmentService;
import com.example.studentscore.service.TeachingClassService;
import com.example.studentscore.vo.EnrollmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选课服务实现类
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements EnrollmentService {

    private final TeachingClassService teachingClassService;

    @Override
    public IPage<Enrollment> pageEnrollments(Integer pageNum, Integer pageSize, Long studentDbId, Long teachingClassDbId) {
        Page<Enrollment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Enrollment> wrapper = new LambdaQueryWrapper<>();
        
        if (studentDbId != null) {
            wrapper.eq(Enrollment::getStudentDbId, studentDbId);
        }
        if (teachingClassDbId != null) {
            wrapper.eq(Enrollment::getTeachingClassDbId, teachingClassDbId);
        }
        // 逻辑删除由 MyBatis-Plus 自动处理，不需要判断 status
        wrapper.orderByDesc(Enrollment::getEnrollTime);
        
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enroll(EnrollmentDTO dto) {
        // 检查是否已选课
        if (isEnrolled(dto.getStudentDbId(), dto.getTeachingClassDbId())) {
            throw new BusinessException(ResultCode.ALREADY_EXISTS, "已选择该课程");
        }
        
        // 检查教学班容量
        TeachingClass teachingClass = teachingClassService.getById(dto.getTeachingClassDbId());
        if (teachingClass == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "教学班不存在");
        }
        if (teachingClass.getCurrentSize() >= teachingClass.getCapacity()) {
            throw new BusinessException("教学班已满");
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentDbId(dto.getStudentDbId());
        enrollment.setTeachingClassDbId(dto.getTeachingClassDbId());
        enrollment.setEnrollTime(LocalDateTime.now());
        // status 字段不在数据库中，不设置
        
        boolean result = save(enrollment);
        if (result) {
            // 更新教学班人数
            teachingClassService.updateClassSize(dto.getTeachingClassDbId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean drop(Long studentDbId, Long teachingClassDbId) {
        Enrollment enrollment = lambdaQuery()
                .eq(Enrollment::getStudentDbId, studentDbId)
                .eq(Enrollment::getTeachingClassDbId, teachingClassDbId)
                .one();
        
        if (enrollment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "未找到选课记录");
        }
        
        // 使用逻辑删除代替 status 字段
        boolean result = removeById(enrollment.getId());
        if (result) {
            // 更新教学班人数
            teachingClassService.updateClassSize(teachingClassDbId);
        }
        return result;
    }

    @Override
    public List<EnrollmentVO> getStudentEnrollments(Long studentDbId) {
        return baseMapper.selectStudentEnrollments(studentDbId);
    }

    @Override
    public List<EnrollmentVO> getClassEnrollments(Long teachingClassDbId) {
        return baseMapper.selectClassEnrollments(teachingClassDbId);
    }

    @Override
    public boolean isEnrolled(Long studentDbId, Long teachingClassDbId) {
        // 逻辑删除由 MyBatis-Plus 自动处理
        return lambdaQuery()
                .eq(Enrollment::getStudentDbId, studentDbId)
                .eq(Enrollment::getTeachingClassDbId, teachingClassDbId)
                .exists();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchEnroll(List<EnrollmentDTO> dtoList) {
        List<Enrollment> enrollments = dtoList.stream().map(dto -> {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudentDbId(dto.getStudentDbId());
            enrollment.setTeachingClassDbId(dto.getTeachingClassDbId());
            enrollment.setEnrollTime(LocalDateTime.now());
            // status 字段不在数据库中，不设置
            return enrollment;
        }).collect(Collectors.toList());
        
        boolean result = saveBatch(enrollments);
        if (result) {
            // 更新所有涉及的教学班人数
            dtoList.stream()
                    .map(EnrollmentDTO::getTeachingClassDbId)
                    .distinct()
                    .forEach(teachingClassService::updateClassSize);
        }
        return result;
    }
}
