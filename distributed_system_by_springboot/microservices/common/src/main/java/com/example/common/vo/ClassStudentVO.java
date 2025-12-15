package com.example.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 教学班学生视图对象（含成绩）
 */
@Data
@Schema(description = "教学班学生视图对象")
public class ClassStudentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "学生数据库ID")
    private Long id;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "成绩信息")
    private ScoreInfo score;

    /**
     * 内部成绩信息类
     */
    @Data
    @Schema(description = "成绩信息")
    public static class ScoreInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "成绩ID")
        private Long id;

        @Schema(description = "平时成绩")
        private BigDecimal regularScore;

        @Schema(description = "期中成绩")
        private BigDecimal midtermScore;

        @Schema(description = "实验成绩")
        private BigDecimal experimentScore;

        @Schema(description = "期末成绩")
        private BigDecimal finalExamScore;

        @Schema(description = "综合成绩")
        private BigDecimal finalScore;
    }
}
