package com.example.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学生成绩详情视图对象
 */
@Data
@Schema(description = "学生成绩详情视图对象")
public class StudentScoreDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "总学分")
    private Integer totalCredits;

    @Schema(description = "获得学分")
    private Integer earnedCredits;

    @Schema(description = "平均绩点")
    private BigDecimal gpa;

    @Schema(description = "平均成绩")
    private BigDecimal averageScore;

    @Schema(description = "成绩列表")
    private List<ScoreVO> scores;
}
