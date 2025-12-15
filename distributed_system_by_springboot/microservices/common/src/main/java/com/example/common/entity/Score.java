package com.example.common.entity;

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

    @TableId(value = "id", type = IdType.NONE)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "学生ID")
    private Long studentDbId;

    @Schema(description = "教学班ID")
    private Long teachingClassDbId;

    @Schema(description = "平时成绩", example = "85.50")
    private BigDecimal regularScore;

    @Schema(description = "期中成绩", example = "80.00")
    private BigDecimal midtermScore;

    @Schema(description = "实验成绩", example = "90.00")
    private BigDecimal experimentScore;

    @Schema(description = "期末成绩", example = "85.00")
    private BigDecimal finalExamScore;

    @Schema(description = "综合成绩", example = "85.00")
    private BigDecimal finalScore;

    @Schema(description = "成绩备注")
    private String remark;

    @Schema(description = "绩点")
    private BigDecimal gradePoint;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标志", hidden = true)
    private Integer deleted;

    /**
     * 计算综合成绩
     * 平时成绩 20% + 期中成绩 20% + 实验成绩 20% + 期末成绩 40%
     */
    public void calculateFinalScore() {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;

        if (regularScore != null) {
            total = total.add(regularScore.multiply(new BigDecimal("0.2")));
            count++;
        }
        if (midtermScore != null) {
            total = total.add(midtermScore.multiply(new BigDecimal("0.2")));
            count++;
        }
        if (experimentScore != null) {
            total = total.add(experimentScore.multiply(new BigDecimal("0.2")));
            count++;
        }
        if (finalExamScore != null) {
            total = total.add(finalExamScore.multiply(new BigDecimal("0.4")));
            count++;
        }

        if (count > 0) {
            this.finalScore = total.setScale(2, BigDecimal.ROUND_HALF_UP);
            this.gradePoint = calculateGradePoint(this.finalScore);
        }
    }

    /**
     * 计算绩点
     */
    private BigDecimal calculateGradePoint(BigDecimal score) {
        if (score == null) return BigDecimal.ZERO;
        double s = score.doubleValue();
        if (s >= 90) return new BigDecimal("4.0");
        if (s >= 85) return new BigDecimal("3.7");
        if (s >= 82) return new BigDecimal("3.3");
        if (s >= 78) return new BigDecimal("3.0");
        if (s >= 75) return new BigDecimal("2.7");
        if (s >= 72) return new BigDecimal("2.3");
        if (s >= 68) return new BigDecimal("2.0");
        if (s >= 64) return new BigDecimal("1.5");
        if (s >= 60) return new BigDecimal("1.0");
        return BigDecimal.ZERO;
    }
}
