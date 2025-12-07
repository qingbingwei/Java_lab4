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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addScore(ScoreDTO dto) {
        // 检查是否已存在该学生在该教学班的成绩
        Score existing = getByStudentAndClass(dto.getStudentDbId(), dto.getTeachingClassDbId());
        if (existing != null) {
            throw new BusinessException(ResultCode.ALREADY_EXISTS, "该学生在该教学班已有成绩记录");
        }
        
        Score score = new Score();
        BeanUtil.copyProperties(dto, score);
        setScoreTimestamps(score, dto);
        score.calculateFinalScore();
        return save(score);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateScore(ScoreDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "成绩ID不能为空");
        }
        
        Score existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "成绩记录不存在");
        }
        
        // 更新各项成绩并记录时间
        updateScoreFields(existing, dto);
        existing.calculateFinalScore();
        return updateById(existing);
    }

    private void setScoreTimestamps(Score score, ScoreDTO dto) {
        // 时间戳字段已移除，使用 create_time 和 update_time 自动填充
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
        List<Score> scores = dtoList.stream().map(dto -> {
            Score score = new Score();
            BeanUtil.copyProperties(dto, score);
            setScoreTimestamps(score, dto);
            score.calculateFinalScore();
            return score;
        }).collect(Collectors.toList());
        return saveBatch(scores);
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
