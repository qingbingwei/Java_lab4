package com.example.studentscore.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 *
 * @author system
 */
@Data
@TableName("score")
@Schema(description = "成绩实体")
public class Score implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Long studentDbId;

    /**
     * 教学班ID
     */
    @Schema(description = "教学班ID")
    private Long teachingClassDbId;

    /**
     * 平时成绩 (20%)
     */
    @Schema(description = "平时成绩", example = "85")
    private Integer regularScore;

    /**
     * 期中成绩 (20%)
     */
    @Schema(description = "期中成绩", example = "80")
    private Integer midtermScore;

    /**
     * 实验成绩 (20%)
     */
    @Schema(description = "实验成绩", example = "90")
    private Integer experimentScore;

    /**
     * 期末成绩 (40%)
     */
    @Schema(description = "期末成绩", example = "85")
    private Integer finalExamScore;

    /**
     * 综合成绩
     */
    @Schema(description = "综合成绩", example = "85.00")
    private BigDecimal finalScore;

    /**
     * 成绩备注
     */
    @Schema(description = "成绩备注")
    private String remark;

    /**
     * 绩点
     */
    @Schema(description = "绩点")
    private BigDecimal gradePoint;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @Schema(description = "删除标志", hidden = true)
    private Integer deleted;

    /**
     * 学生信息（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学生信息")
    private Student student;

    /**
     * 教学班信息（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教学班信息")
    private TeachingClass teachingClass;

    /**
     * 计算综合成绩
     * 综合成绩 = 平时成绩*0.2 + 期中成绩*0.2 + 实验成绩*0.2 + 期末成绩*0.4
     */
    public void calculateFinalScore() {
        if (regularScore != null && midtermScore != null && 
            experimentScore != null && finalExamScore != null) {
            double score = regularScore * 0.2 + midtermScore * 0.2 + 
                          experimentScore * 0.2 + finalExamScore * 0.4;
            this.finalScore = BigDecimal.valueOf(score).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }
}
