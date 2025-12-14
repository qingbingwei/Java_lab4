package com.example.studentscore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.common.ResultCode;
import com.example.studentscore.dto.StudentDTO;
import com.example.studentscore.entity.Enrollment;
import com.example.studentscore.entity.Score;
import com.example.studentscore.entity.Student;
import com.example.studentscore.entity.User;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.EnrollmentMapper;
import com.example.studentscore.mapper.ScoreMapper;
import com.example.studentscore.mapper.StudentMapper;
import com.example.studentscore.mapper.UserMapper;
import com.example.studentscore.query.StudentQuery;
import com.example.studentscore.service.ScoreService;
import com.example.studentscore.service.StudentService;
import com.example.studentscore.vo.ScoreVO;
import com.example.studentscore.vo.StudentScoreDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生服务实现类
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Lazy
    private final ScoreService scoreService;
    
    private final EnrollmentMapper enrollmentMapper;
    private final ScoreMapper scoreMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<Student> pageStudents(StudentQuery query) {
        Page<Student> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StrUtil.isNotBlank(query.getStudentId()), Student::getStudentId, query.getStudentId())
               .like(StrUtil.isNotBlank(query.getName()), Student::getName, query.getName())
               .eq(StrUtil.isNotBlank(query.getGender()), Student::getGender, query.getGender())
               .like(StrUtil.isNotBlank(query.getClassName()), Student::getClassName, query.getClassName())
               .eq(StrUtil.isNotBlank(query.getGrade()), Student::getGrade, query.getGrade())
               .like(StrUtil.isNotBlank(query.getMajor()), Student::getMajor, query.getMajor());
        
        // 关键词搜索（学号或姓名）
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(Student::getStudentId, query.getKeyword())
                             .or()
                             .like(Student::getName, query.getKeyword()));
        }
        
        wrapper.orderByDesc(Student::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Student getByStudentId(String studentId) {
        return lambdaQuery().eq(Student::getStudentId, studentId).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStudent(StudentDTO dto) {
        // 检查学号是否已存在
        if (getByStudentId(dto.getStudentId()) != null) {
            throw new BusinessException(ResultCode.ALREADY_EXISTS, "学号已存在: " + dto.getStudentId());
        }
        
        Student student = new Student();
        BeanUtil.copyProperties(dto, student);
        student.setId(null); // 确保新增时ID为null，让数据库自动生成
        return save(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(StudentDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "学生ID不能为空");
        }
        
        Student existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
        }
        
        // 如果修改了学号，检查新学号是否已存在
        if (!existing.getStudentId().equals(dto.getStudentId())) {
            if (getByStudentId(dto.getStudentId()) != null) {
                throw new BusinessException(ResultCode.ALREADY_EXISTS, "学号已存在: " + dto.getStudentId());
            }
        }
        
        BeanUtil.copyProperties(dto, existing);
        return updateById(existing);
    }

    /**
     * 删除学生时级联删除关联数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        Long studentId = Long.valueOf(id.toString());
        // 删除学生的选课记录
        enrollmentMapper.delete(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getStudentDbId, studentId));
        // 删除学生的成绩记录
        scoreMapper.delete(new LambdaQueryWrapper<Score>()
                .eq(Score::getStudentDbId, studentId));
        // 删除关联的用户账号
        userMapper.delete(new LambdaQueryWrapper<User>()
                .eq(User::getRefId, studentId)
                .eq(User::getRole, "STUDENT"));
        // 删除学生本身
        return super.removeById(id);
    }

    /**
     * 批量删除学生时级联删除关联数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> list) {
        for (Object id : list) {
            removeById((Serializable) id);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddStudents(List<StudentDTO> dtoList) {
        List<Student> students = dtoList.stream().map(dto -> {
            Student student = new Student();
            BeanUtil.copyProperties(dto, student);
            return student;
        }).collect(Collectors.toList());
        return saveBatch(students);
    }

    @Override
    public StudentScoreDetailVO getStudentScoreDetail(Long id) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
        }
        return buildStudentScoreDetail(student);
    }

    @Override
    public StudentScoreDetailVO getStudentScoreDetailByStudentId(String studentId) {
        Student student = getByStudentId(studentId);
        if (student == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
        }
        return buildStudentScoreDetail(student);
    }

    private StudentScoreDetailVO buildStudentScoreDetail(Student student) {
        StudentScoreDetailVO vo = new StudentScoreDetailVO();
        vo.setStudentId(student.getStudentId());
        vo.setStudentName(student.getName());
        vo.setGender(student.getGender());
        vo.setClassName(student.getClassName());
        vo.setMajor(student.getMajor());
        
        // 获取成绩列表
        List<ScoreVO> scores = scoreService.getStudentScores(student.getId());
        vo.setScores(scores);
        
        // 计算统计信息
        if (!scores.isEmpty()) {
            // 计算平均成绩
            double avgScore = scores.stream()
                    .filter(s -> s.getFinalScore() != null)
                    .mapToDouble(s -> s.getFinalScore().doubleValue())
                    .average()
                    .orElse(0);
            vo.setAverageScore(BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP));
            
            // 计算GPA（简化计算：90-100=4.0, 80-89=3.0, 70-79=2.0, 60-69=1.0, <60=0）
            double gpa = scores.stream()
                    .filter(s -> s.getFinalScore() != null)
                    .mapToDouble(s -> {
                        double score = s.getFinalScore().doubleValue();
                        if (score >= 90) return 4.0;
                        if (score >= 80) return 3.0;
                        if (score >= 70) return 2.0;
                        if (score >= 60) return 1.0;
                        return 0;
                    })
                    .average()
                    .orElse(0);
            vo.setGpa(BigDecimal.valueOf(gpa).setScale(2, RoundingMode.HALF_UP));
        }
        
        return vo;
    }
}
