package com.example.studentscore.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.common.ResultCode;
import com.example.studentscore.dto.ScoreDTO;
import com.example.studentscore.entity.Score;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.ScoreMapper;
import com.example.studentscore.query.ScoreQuery;
import com.example.studentscore.service.ScoreService;
import com.example.studentscore.vo.CourseStatisticsVO;
import com.example.studentscore.vo.ScoreVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 *
 * @author system
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    @Override
    public IPage<ScoreVO> pageScores(ScoreQuery query) {
        Page<ScoreVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return baseMapper.selectScorePage(page, query);
    }

    @Override
    public List<ScoreVO> getStudentScores(Long studentDbId) {
        return baseMapper.selectStudentScores(studentDbId);
    }

    @Override
    public Score getByStudentAndClass(Long studentDbId, Long teachingClassDbId) {
        return lambdaQuery()
                .eq(Score::getStudentDbId, studentDbId)
                .eq(Score::getTeachingClassDbId, teachingClassDbId)
                .one();
    }
    
    /**
     * 查询成绩记录（包括已删除的）
     */
    private Score getByStudentAndClassIncludeDeleted(Long studentDbId, Long teachingClassDbId) {
        return baseMapper.selectOne(
            new LambdaQueryWrapper<Score>()
                .eq(Score::getStudentDbId, studentDbId)
                .eq(Score::getTeachingClassDbId, teachingClassDbId)
                .last("LIMIT 1")  // 避免唯一约束下可能的多条记录问题
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addScore(ScoreDTO dto) {
        // 检查是否已存在该学生在该教学班的成绩（包括已删除的）
        Score existing = getByStudentAndClassIncludeDeleted(dto.getStudentDbId(), dto.getTeachingClassDbId());
        if (existing != null) {
            // 如果存在（无论是否已删除），则更新该记录
            if (existing.getDeleted() != null && existing.getDeleted() == 1) {
                // 恢复已删除的记录
                existing.setDeleted(0);
            }
            // 更新成绩字段
            updateScoreFieldsDirectly(existing, dto);
            existing.calculateFinalScore();
            baseMapper.updateById(existing);
            return existing.getId();
        }
        
        // 创建新成绩记录
        Score score = new Score();
        BeanUtil.copyProperties(dto, score);
        setScoreTimestamps(score, dto);
        score.calculateFinalScore();
        save(score);
        return score.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateScore(ScoreDTO dto) {
        Score existing;
        
        if (dto.getId() != null) {
            // 先尝试通过 ID 查找
            existing = getById(dto.getId());
        } else {
            existing = null;
        }
        
        // 如果通过 ID 找不到，尝试通过 studentDbId 和 teachingClassDbId 查找
        if (existing == null && dto.getStudentDbId() != null && dto.getTeachingClassDbId() != null) {
            existing = getByStudentAndClass(dto.getStudentDbId(), dto.getTeachingClassDbId());
        }
        
        if (existing == null) {
            // 如果仍然找不到，则创建新记录
            addScore(dto);
            return true;
        }
        
        // 更新各项成绩
        updateScoreFieldsDirectly(existing, dto);
        existing.calculateFinalScore();
        return updateById(existing);
    }

    private void setScoreTimestamps(Score score, ScoreDTO dto) {
        // 时间戳字段已移除，使用 create_time 和 update_time 自动填充
    }
    
    /**
     * 直接更新成绩字段（不检查是否变化）
     */
    private void updateScoreFieldsDirectly(Score existing, ScoreDTO dto) {
        if (dto.getRegularScore() != null) {
            existing.setRegularScore(dto.getRegularScore());
        }
        if (dto.getMidtermScore() != null) {
            existing.setMidtermScore(dto.getMidtermScore());
        }
        if (dto.getExperimentScore() != null) {
            existing.setExperimentScore(dto.getExperimentScore());
        }
        if (dto.getFinalExamScore() != null) {
            existing.setFinalExamScore(dto.getFinalExamScore());
        }
    }

    private void updateScoreFields(Score existing, ScoreDTO dto) {
        if (dto.getRegularScore() != null && !dto.getRegularScore().equals(existing.getRegularScore())) {
            existing.setRegularScore(dto.getRegularScore());
        }
        if (dto.getMidtermScore() != null && !dto.getMidtermScore().equals(existing.getMidtermScore())) {
            existing.setMidtermScore(dto.getMidtermScore());
        }
        if (dto.getExperimentScore() != null && !dto.getExperimentScore().equals(existing.getExperimentScore())) {
            existing.setExperimentScore(dto.getExperimentScore());
        }
        if (dto.getFinalExamScore() != null && !dto.getFinalExamScore().equals(existing.getFinalExamScore())) {
            existing.setFinalExamScore(dto.getFinalExamScore());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddScores(List<ScoreDTO> dtoList) {
        for (ScoreDTO dto : dtoList) {
            // 检查是否已存在（包括已删除的）
            Score existing = getByStudentAndClassIncludeDeleted(dto.getStudentDbId(), dto.getTeachingClassDbId());
            if (existing != null) {
                // 如果存在（无论是否已删除），则更新该记录
                if (existing.getDeleted() != null && existing.getDeleted() == 1) {
                    existing.setDeleted(0);
                }
                updateScoreFieldsDirectly(existing, dto);
                existing.calculateFinalScore();
                baseMapper.updateById(existing);
            } else {
                // 不存在则创建
                Score score = new Score();
                BeanUtil.copyProperties(dto, score);
                setScoreTimestamps(score, dto);
                score.calculateFinalScore();
                save(score);
            }
        }
        return true;
    }

    @Override
    public List<ScoreVO> getScoreRanking(ScoreQuery query) {
        return baseMapper.selectScoreRanking(query);
    }

    @Override
    public List<CourseStatisticsVO> getCourseStatistics(String semester) {
        return baseMapper.selectCourseStatistics(semester);
    }
}
