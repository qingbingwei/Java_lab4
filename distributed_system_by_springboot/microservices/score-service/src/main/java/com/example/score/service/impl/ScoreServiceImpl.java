package com.example.score.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.dto.ScoreDTO;
import com.example.common.entity.*;
import com.example.common.exception.BusinessException;
import com.example.common.query.ScoreQuery;
import com.example.common.vo.ScoreVO;
import com.example.common.vo.StudentScoreDetailVO;
import com.example.score.mapper.*;
import com.example.score.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 成绩服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    
    private final ScoreMapper scoreMapper;
    private final StudentMapper studentMapper;
    private final TeachingClassMapper teachingClassMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final EnrollmentMapper enrollmentMapper;
    
    @Override
    public IPage<ScoreVO> page(ScoreQuery query) {
        Page<Score> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Score> wrapper = buildQueryWrapper(query);
        wrapper.orderByDesc(Score::getCreateTime);
        
        IPage<Score> result = scoreMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<ScoreVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(convertToVOList(result.getRecords()));
        
        return voPage;
    }
    
    @Override
    public List<ScoreVO> list() {
        List<Score> list = scoreMapper.selectList(null);
        return convertToVOList(list);
    }
    
    @Override
    public ScoreVO getById(String id) {
        Score score = scoreMapper.selectById(id);
        return score != null ? convertToVO(score) : null;
    }
    
    @Override
    public List<ScoreVO> getByStudentId(String studentId) {
        // 先查询学生获取数据库ID
        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(Student::getStudentId, studentId);
        Student student = studentMapper.selectOne(studentWrapper);
        if (student == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentDbId, student.getId());
        List<Score> list = scoreMapper.selectList(wrapper);
        return convertToVOList(list);
    }
    
    @Override
    public List<ScoreVO> getByClassId(String classId) {
        // 先查询教学班获取数据库ID
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass teachingClass = teachingClassMapper.selectOne(tcWrapper);
        if (teachingClass == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
        wrapper.orderByDesc(Score::getFinalScore);
        List<Score> list = scoreMapper.selectList(wrapper);
        return convertToVOList(list);
    }
    
    @Override
    public StudentScoreDetailVO getStudentScoreDetail(String studentId) {
        // 查询学生信息
        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(Student::getStudentId, studentId);
        Student student = studentMapper.selectOne(studentWrapper);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        StudentScoreDetailVO vo = new StudentScoreDetailVO();
        vo.setStudentId(student.getStudentId());
        vo.setStudentName(student.getName());
        vo.setMajor(student.getMajor());
        vo.setClassName(student.getClassName());
        
        // 查询成绩列表
        List<ScoreVO> scores = getByStudentId(studentId);
        vo.setScores(scores);
        
        // 计算统计信息
        if (!scores.isEmpty()) {
            BigDecimal totalScore = BigDecimal.ZERO;
            BigDecimal totalCredits = BigDecimal.ZERO;
            BigDecimal weightedScore = BigDecimal.ZERO;
            
            for (ScoreVO score : scores) {
                if (score.getScore() != null) {
                    totalScore = totalScore.add(score.getScore());
                    if (score.getCredits() != null) {
                        weightedScore = weightedScore.add(score.getScore().multiply(score.getCredits()));
                        totalCredits = totalCredits.add(score.getCredits());
                    }
                }
            }
            
            vo.setAverageScore(totalScore.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP));
            
            if (totalCredits.compareTo(BigDecimal.ZERO) > 0) {
                vo.setTotalCredits(totalCredits.setScale(1, RoundingMode.HALF_UP).intValue());
                vo.setGpa(weightedScore.divide(totalCredits, 2, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP));
            }
        } else {
            vo.setAverageScore(BigDecimal.ZERO);
            vo.setTotalCredits(0);
            vo.setGpa(BigDecimal.ZERO);
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ScoreDTO dto) {
        // 检查成绩是否已存在（包含已删除的记录，因为唯一索引是物理层面的）
        Score existing = scoreMapper.selectByStudentAndClassIgnoreDeleted(
            dto.getStudentDbId(), dto.getTeachingClassDbId());
        
        log.info("检查成绩是否存在: studentDbId={}, teachingClassDbId={}, existing={}", 
            dto.getStudentDbId(), dto.getTeachingClassDbId(), existing != null);
        
        if (existing != null) {
            // 成绩已存在，执行更新操作（同时恢复软删除的记录）
            log.info("成绩已存在，执行更新操作, existingId={}, deleted={}", existing.getId(), existing.getDeleted());
            BeanUtils.copyProperties(dto, existing, "id", "studentDbId", "teachingClassDbId", "createTime");
            existing.setDeleted(0); // 恢复软删除的记录
            calculateFinalScore(existing);
            // 使用忽略逻辑删除的更新方法，确保能更新已删除的记录
            return scoreMapper.updateByIdIgnoreDeleted(existing) > 0;
        }
        
        // 检查学生是否已选课
        LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
        enrollmentWrapper.eq(Enrollment::getStudentDbId, dto.getStudentDbId());
        enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, dto.getTeachingClassDbId());
        Enrollment enrollment = enrollmentMapper.selectOne(enrollmentWrapper);
        if (enrollment == null) {
            throw new BusinessException("该学生未选此教学班");
        }
        
        log.info("成绩不存在，执行新增操作");
        Score score = new Score();
        BeanUtils.copyProperties(dto, score);
        
        // 计算综合成绩
        calculateFinalScore(score);
        
        return scoreMapper.insert(score) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<ScoreDTO> dtos) {
        for (ScoreDTO dto : dtos) {
            // 检查成绩是否已存在（包含已删除的记录）
            Score existing = scoreMapper.selectByStudentAndClassIgnoreDeleted(
                dto.getStudentDbId(), dto.getTeachingClassDbId());
            
            if (existing != null) {
                // 更新成绩 - 保留原有的id和关键字段，恢复软删除
                BeanUtils.copyProperties(dto, existing, "id", "studentDbId", "teachingClassDbId", "createTime");
                existing.setDeleted(0); // 恢复软删除的记录
                // 计算综合成绩
                calculateFinalScore(existing);
                // 使用忽略逻辑删除的更新方法
                scoreMapper.updateByIdIgnoreDeleted(existing);
            } else {
                // 检查学生是否已选课
                LambdaQueryWrapper<Enrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
                enrollmentWrapper.eq(Enrollment::getStudentDbId, dto.getStudentDbId());
                enrollmentWrapper.eq(Enrollment::getTeachingClassDbId, dto.getTeachingClassDbId());
                if (enrollmentMapper.selectCount(enrollmentWrapper) > 0) {
                    Score score = new Score();
                    BeanUtils.copyProperties(dto, score);
                    // 计算综合成绩
                    calculateFinalScore(score);
                    scoreMapper.insert(score);
                }
            }
        }
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(ScoreDTO dto) {
        Score existing = scoreMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("成绩记录不存在");
        }
        
        BeanUtils.copyProperties(dto, existing);
        // 计算综合成绩
        calculateFinalScore(existing);
        return scoreMapper.updateById(existing) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String id) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new BusinessException("成绩记录不存在");
        }
        
        return scoreMapper.deleteById(id) > 0;
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
        // 查询学生ID
        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(Student::getStudentId, studentId);
        Student student = studentMapper.selectOne(studentWrapper);
        if (student == null) return false;
        
        // 查询教学班ID
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass teachingClass = teachingClassMapper.selectOne(tcWrapper);
        if (teachingClass == null) return false;
        
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentDbId, student.getId());
        wrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
        return scoreMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public long count() {
        return scoreMapper.selectCount(null);
    }
    
    @Override
    public List<ScoreVO> getRanking(String classId) {
        // 查询教学班数据库ID
        LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
        tcWrapper.eq(TeachingClass::getClassId, classId);
        TeachingClass teachingClass = teachingClassMapper.selectOne(tcWrapper);
        if (teachingClass == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getTeachingClassDbId, teachingClass.getId());
        wrapper.orderByDesc(Score::getFinalScore);
        List<Score> list = scoreMapper.selectList(wrapper);
        
        List<ScoreVO> voList = convertToVOList(list);
        
        // 设置排名
        int rank = 1;
        for (ScoreVO vo : voList) {
            vo.setClassRank(rank++);
        }
        
        return voList;
    }
    
    @Override
    public List<ScoreVO> getRankingAll(Long teachingClassDbId, String semester) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        
        // 如果指定了教学班ID，按教学班过滤
        if (teachingClassDbId != null) {
            wrapper.eq(Score::getTeachingClassDbId, teachingClassDbId);
        }
        
        // 如果指定了学期，需要通过教学班过滤
        if (StringUtils.hasText(semester)) {
            LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
            tcWrapper.eq(TeachingClass::getSemester, semester);
            List<TeachingClass> teachingClasses = teachingClassMapper.selectList(tcWrapper);
            if (!teachingClasses.isEmpty()) {
                List<Long> classDbIds = teachingClasses.stream().map(TeachingClass::getId).toList();
                wrapper.in(Score::getTeachingClassDbId, classDbIds);
            } else {
                return new ArrayList<>();
            }
        }
        
        wrapper.orderByDesc(Score::getFinalScore);
        List<Score> list = scoreMapper.selectList(wrapper);
        
        List<ScoreVO> voList = convertToVOList(list);
        
        // 设置排名
        int rank = 1;
        for (ScoreVO vo : voList) {
            vo.setClassRank(rank++);
        }
        
        return voList;
    }
    
    @Override
    public List<ScoreVO> getStudentRanking(String studentId) {
        List<ScoreVO> scores = getByStudentId(studentId);
        
        for (ScoreVO score : scores) {
            // 获取该教学班的排名
            List<ScoreVO> classRanking = getRanking(score.getClassId());
            for (int i = 0; i < classRanking.size(); i++) {
                if (classRanking.get(i).getStudentId().equals(studentId)) {
                    score.setClassRank(i + 1);
                    break;
                }
            }
        }
        
        return scores;
    }
    
    private LambdaQueryWrapper<Score> buildQueryWrapper(ScoreQuery query) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        
        // 优先使用数据库ID进行精确查询（学生只能查自己的成绩）
        if (query.getStudentDbId() != null) {
            wrapper.eq(Score::getStudentDbId, query.getStudentDbId());
        }
        
        if (StringUtils.hasText(query.getStudentId())) {
            // 查询学生数据库ID
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.like(Student::getStudentId, query.getStudentId());
            List<Student> students = studentMapper.selectList(studentWrapper);
            if (!students.isEmpty()) {
                List<Long> studentDbIds = students.stream().map(Student::getId).toList();
                wrapper.in(Score::getStudentDbId, studentDbIds);
            } else {
                wrapper.isNull(Score::getId);
            }
        }
        if (StringUtils.hasText(query.getClassId())) {
            // 查询教学班数据库ID
            LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
            tcWrapper.eq(TeachingClass::getClassId, query.getClassId());
            TeachingClass tc = teachingClassMapper.selectOne(tcWrapper);
            if (tc != null) {
                wrapper.eq(Score::getTeachingClassDbId, tc.getId());
            } else {
                wrapper.isNull(Score::getId);
            }
        }
        if (StringUtils.hasText(query.getStudentName())) {
            // 查询学生
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.like(Student::getName, query.getStudentName());
            List<Student> students = studentMapper.selectList(studentWrapper);
            if (!students.isEmpty()) {
                List<Long> studentDbIds = students.stream().map(Student::getId).toList();
                wrapper.in(Score::getStudentDbId, studentDbIds);
            } else {
                wrapper.isNull(Score::getId);
            }
        }
        if (StringUtils.hasText(query.getCourseName())) {
            // 查询课程和教学班
            LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.like(Course::getCourseName, query.getCourseName());
            List<Course> courses = courseMapper.selectList(courseWrapper);
            if (!courses.isEmpty()) {
                List<Long> courseDbIds = courses.stream().map(Course::getId).toList();
                LambdaQueryWrapper<TeachingClass> tcWrapper = new LambdaQueryWrapper<>();
                tcWrapper.in(TeachingClass::getCourseDbId, courseDbIds);
                List<TeachingClass> teachingClasses = teachingClassMapper.selectList(tcWrapper);
                if (!teachingClasses.isEmpty()) {
                    List<Long> classDbIds = teachingClasses.stream().map(TeachingClass::getId).toList();
                    wrapper.in(Score::getTeachingClassDbId, classDbIds);
                } else {
                    wrapper.isNull(Score::getId);
                }
            } else {
                wrapper.isNull(Score::getId);
            }
        }
        if (query.getMinScore() != null) {
            wrapper.ge(Score::getFinalScore, query.getMinScore());
        }
        if (query.getMaxScore() != null) {
            wrapper.le(Score::getFinalScore, query.getMaxScore());
        }
        
        return wrapper;
    }
    
    private ScoreVO convertToVO(Score score) {
        ScoreVO vo = new ScoreVO();
        BeanUtils.copyProperties(score, vo);
        
        // 设置score字段为finalScore用于前端显示
        vo.setScore(score.getFinalScore());
        
        // 查询学生信息
        Student student = studentMapper.selectById(score.getStudentDbId());
        if (student != null) {
            vo.setStudentId(student.getStudentId());
            vo.setStudentName(student.getName());
        }
        
        // 查询教学班信息
        TeachingClass teachingClass = teachingClassMapper.selectById(score.getTeachingClassDbId());
        if (teachingClass != null) {
            vo.setClassId(teachingClass.getClassId());
            vo.setClassName(teachingClass.getClassId());
            vo.setSemester(teachingClass.getSemester());
            
            // 查询课程信息
            Course course = courseMapper.selectById(teachingClass.getCourseDbId());
            if (course != null) {
                vo.setCourseCode(course.getCourseId());
                vo.setCourseName(course.getCourseName());
                vo.setCredits(course.getCredits());
            }
            
            // 查询教师信息
            Teacher teacher = teacherMapper.selectById(teachingClass.getTeacherDbId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
            }
        }
        
        return vo;
    }
    
    private List<ScoreVO> convertToVOList(List<Score> list) {
        List<ScoreVO> voList = new ArrayList<>();
        for (Score score : list) {
            voList.add(convertToVO(score));
        }
        return voList;
    }
    
    /**
     * 计算综合成绩
     * 权重：平时成绩20%，期中成绩20%，实验成绩20%，期末成绩40%
     */
    private void calculateFinalScore(Score score) {
        BigDecimal finalScore = BigDecimal.ZERO;
        int componentCount = 0;
        
        // 平时成绩 20%
        if (score.getRegularScore() != null) {
            finalScore = finalScore.add(score.getRegularScore().multiply(new BigDecimal("0.20")));
            componentCount++;
        }
        // 期中成绩 20%
        if (score.getMidtermScore() != null) {
            finalScore = finalScore.add(score.getMidtermScore().multiply(new BigDecimal("0.20")));
            componentCount++;
        }
        // 实验成绩 20%
        if (score.getExperimentScore() != null) {
            finalScore = finalScore.add(score.getExperimentScore().multiply(new BigDecimal("0.20")));
            componentCount++;
        }
        // 期末成绩 40%
        if (score.getFinalExamScore() != null) {
            finalScore = finalScore.add(score.getFinalExamScore().multiply(new BigDecimal("0.40")));
            componentCount++;
        }
        
        // 如果有成绩项，设置综合成绩
        if (componentCount > 0) {
            // 如果不是所有成绩都有，按实际比例计算
            if (componentCount < 4) {
                // 简化处理：直接使用已有成绩的加权平均
                BigDecimal totalWeight = BigDecimal.ZERO;
                finalScore = BigDecimal.ZERO;
                
                if (score.getRegularScore() != null) {
                    finalScore = finalScore.add(score.getRegularScore().multiply(new BigDecimal("0.20")));
                    totalWeight = totalWeight.add(new BigDecimal("0.20"));
                }
                if (score.getMidtermScore() != null) {
                    finalScore = finalScore.add(score.getMidtermScore().multiply(new BigDecimal("0.20")));
                    totalWeight = totalWeight.add(new BigDecimal("0.20"));
                }
                if (score.getExperimentScore() != null) {
                    finalScore = finalScore.add(score.getExperimentScore().multiply(new BigDecimal("0.20")));
                    totalWeight = totalWeight.add(new BigDecimal("0.20"));
                }
                if (score.getFinalExamScore() != null) {
                    finalScore = finalScore.add(score.getFinalExamScore().multiply(new BigDecimal("0.40")));
                    totalWeight = totalWeight.add(new BigDecimal("0.40"));
                }
                
                if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
                    finalScore = finalScore.divide(totalWeight, 2, RoundingMode.HALF_UP).multiply(BigDecimal.ONE);
                }
            }
            
            score.setFinalScore(finalScore.setScale(2, RoundingMode.HALF_UP));
            
            // 计算绩点 (满分100，绩点4.0)
            BigDecimal gradePoint = finalScore.subtract(new BigDecimal("50"))
                    .divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP);
            if (gradePoint.compareTo(BigDecimal.ZERO) < 0) {
                gradePoint = BigDecimal.ZERO;
            }
            if (gradePoint.compareTo(new BigDecimal("4.0")) > 0) {
                gradePoint = new BigDecimal("4.0");
            }
            score.setGradePoint(gradePoint);
        }
    }
}
