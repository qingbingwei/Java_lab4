package com.example.studentscore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.common.ResultCode;
import com.example.studentscore.dto.TeachingClassDTO;
import com.example.studentscore.entity.Enrollment;
import com.example.studentscore.entity.Score;
import com.example.studentscore.entity.Student;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.EnrollmentMapper;
import com.example.studentscore.mapper.ScoreMapper;
import com.example.studentscore.mapper.StudentMapper;
import com.example.studentscore.mapper.TeachingClassMapper;
import com.example.studentscore.query.TeachingClassQuery;
import com.example.studentscore.service.TeachingClassService;
import com.example.studentscore.vo.TeachingClassVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学班服务实现类
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class TeachingClassServiceImpl extends ServiceImpl<TeachingClassMapper, TeachingClass> implements TeachingClassService {

    private final EnrollmentMapper enrollmentMapper;
    private final StudentMapper studentMapper;
    private final ScoreMapper scoreMapper;

    @Override
    public IPage<TeachingClassVO> pageTeachingClasses(TeachingClassQuery query) {
        Page<TeachingClassVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return baseMapper.selectTeachingClassPage(page, query);
    }

    @Override
    public TeachingClass getByClassId(String classId) {
        return lambdaQuery().eq(TeachingClass::getClassId, classId).one();
    }

    @Override
    public TeachingClassVO getTeachingClassDetail(Long id) {
        TeachingClassVO vo = baseMapper.selectTeachingClassById(id);
        if (vo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "教学班不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeachingClass(TeachingClassDTO dto) {
        // 检查教学班号是否已存在
        if (getByClassId(dto.getClassId()) != null) {
            throw new BusinessException(ResultCode.ALREADY_EXISTS, "教学班号已存在: " + dto.getClassId());
        }
        
        TeachingClass teachingClass = new TeachingClass();
        BeanUtil.copyProperties(dto, teachingClass);
        teachingClass.setId(null); // 确保新增时ID为null，让数据库自动生成
        teachingClass.setCurrentSize(0);
        return save(teachingClass);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTeachingClass(TeachingClassDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "教学班ID不能为空");
        }
        
        TeachingClass existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "教学班不存在");
        }
        
        // 如果修改了班号，检查新班号是否已存在
        if (!existing.getClassId().equals(dto.getClassId())) {
            if (getByClassId(dto.getClassId()) != null) {
                throw new BusinessException(ResultCode.ALREADY_EXISTS, "教学班号已存在: " + dto.getClassId());
            }
        }
        
        BeanUtil.copyProperties(dto, existing, "currentSize");
        return updateById(existing);
    }

    @Override
    public List<TeachingClassVO> getTeacherClasses(Long teacherDbId) {
        TeachingClassQuery query = new TeachingClassQuery();
        query.setPageNum(1);
        query.setPageSize(1000);
        
        LambdaQueryWrapper<TeachingClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingClass::getTeacherDbId, teacherDbId);
        
        List<TeachingClass> classes = list(wrapper);
        return classes.stream().map(tc -> {
            TeachingClassVO vo = baseMapper.selectTeachingClassById(tc.getId());
            return vo;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateClassSize(Long id) {
        // currentSize 不在数据库中，是动态计算的，所以这里不需要更新
        // 保留方法以保持接口兼容性
    }

    @Override
    public List<Object> getClassStudentsWithScores(Long teachingClassDbId) {
        // 1. 获取该教学班的所有选课记录（未删除的即为已选）
        List<Enrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<Enrollment>()
                        .eq(Enrollment::getTeachingClassDbId, teachingClassDbId)
        );
        
        // 2. 获取每个学生的信息和成绩
        return enrollments.stream().map(enrollment -> {
            Map<String, Object> result = new HashMap<>();
            
            // 获取学生信息
            Student student = studentMapper.selectById(enrollment.getStudentDbId());
            if (student != null) {
                result.put("id", student.getId());
                result.put("studentId", student.getStudentId());
                result.put("name", student.getName());
                result.put("className", student.getClassName());
            }
            
            // 获取该学生在此教学班的成绩
            Score score = scoreMapper.selectOne(
                    new LambdaQueryWrapper<Score>()
                            .eq(Score::getStudentDbId, enrollment.getStudentDbId())
                            .eq(Score::getTeachingClassDbId, teachingClassDbId)
            );
            if (score != null) {
                Map<String, Object> scoreMap = new HashMap<>();
                scoreMap.put("id", score.getId());
                scoreMap.put("regularScore", score.getRegularScore());
                scoreMap.put("midtermScore", score.getMidtermScore());
                scoreMap.put("experimentScore", score.getExperimentScore());
                scoreMap.put("finalExamScore", score.getFinalExamScore());
                scoreMap.put("finalScore", score.getFinalScore());
                result.put("score", scoreMap);
            }
            
            return (Object) result;
        }).toList();
    }

    @Override
    public List<TeachingClassVO> listAllTeachingClasses() {
        return baseMapper.selectAllTeachingClasses();
    }

    /**
     * 删除教学班时级联删除选课记录和成绩
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        Long teachingClassId = Long.valueOf(id.toString());
        // 删除该教学班的所有成绩记录
        scoreMapper.delete(new LambdaQueryWrapper<Score>()
                .eq(Score::getTeachingClassDbId, teachingClassId));
        // 删除该教学班的所有选课记录
        enrollmentMapper.delete(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getTeachingClassDbId, teachingClassId));
        // 删除教学班本身
        return super.removeById(id);
    }

    /**
     * 批量删除教学班
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
