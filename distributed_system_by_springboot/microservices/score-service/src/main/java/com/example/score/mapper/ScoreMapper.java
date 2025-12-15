package com.example.score.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 成绩Mapper
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    
    /**
     * 查询成绩（包含已删除的记录），用于唯一性检查
     */
    @Select("SELECT * FROM score WHERE student_db_id = #{studentDbId} AND teaching_class_db_id = #{teachingClassDbId} LIMIT 1")
    Score selectByStudentAndClassIgnoreDeleted(@Param("studentDbId") Long studentDbId, 
                                                @Param("teachingClassDbId") Long teachingClassDbId);
    
    /**
     * 更新成绩（忽略逻辑删除条件），用于恢复和更新已删除的记录
     */
    @Update("UPDATE score SET regular_score = #{score.regularScore}, midterm_score = #{score.midtermScore}, " +
            "experiment_score = #{score.experimentScore}, final_exam_score = #{score.finalExamScore}, " +
            "final_score = #{score.finalScore}, grade_point = #{score.gradePoint}, " +
            "deleted = #{score.deleted}, update_time = datetime('now', 'localtime') " +
            "WHERE id = #{score.id}")
    int updateByIdIgnoreDeleted(@Param("score") Score score);
}
