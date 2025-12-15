package com.example.score.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.dto.ScoreDTO;
import com.example.common.query.ScoreQuery;
import com.example.common.vo.ScoreVO;
import com.example.common.vo.StudentScoreDetailVO;

import java.util.List;

/**
 * 成绩服务接口
 */
public interface ScoreService {
    
    /**
     * 分页查询成绩
     */
    IPage<ScoreVO> page(ScoreQuery query);
    
    /**
     * 获取所有成绩
     */
    List<ScoreVO> list();
    
    /**
     * 根据ID获取成绩
     */
    ScoreVO getById(String id);
    
    /**
     * 根据学号获取成绩列表
     */
    List<ScoreVO> getByStudentId(String studentId);
    
    /**
     * 根据教学班获取成绩列表
     */
    List<ScoreVO> getByClassId(String classId);
    
    /**
     * 获取学生成绩详情
     */
    StudentScoreDetailVO getStudentScoreDetail(String studentId);
    
    /**
     * 新增成绩
     */
    boolean save(ScoreDTO dto);
    
    /**
     * 批量新增成绩
     */
    boolean saveBatch(List<ScoreDTO> dtos);
    
    /**
     * 更新成绩
     */
    boolean update(ScoreDTO dto);
    
    /**
     * 删除成绩
     */
    boolean delete(String id);
    
    /**
     * 批量删除成绩
     */
    boolean deleteBatch(List<String> ids);
    
    /**
     * 检查成绩是否存在
     */
    boolean exists(String studentId, String classId);
    
    /**
     * 统计成绩总数
     */
    long count();
    
    /**
     * 获取班级成绩排名
     */
    List<ScoreVO> getRanking(String classId);
    
    /**
     * 获取成绩排名（支持可选筛选条件）
     */
    List<ScoreVO> getRankingAll(Long teachingClassDbId, String semester);
    
    /**
     * 获取学生综合排名
     */
    List<ScoreVO> getStudentRanking(String studentId);
}
