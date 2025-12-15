package com.example.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 成绩视图对象
 */
@Data
@Schema(description = "成绩视图对象")
public class ScoreVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "成绩ID")
    private Long id;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "教学班号")
    private String classId;

    @Schema(description = "教学班名称")
    private String className;

    @Schema(description = "课程代码")
    private String courseCode;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "学分")
    private BigDecimal credits;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "学期")
    private String semester;

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

    @Schema(description = "成绩(用于前端显示)")
    private BigDecimal score;

    @Schema(description = "绩点")
    private BigDecimal gradePoint;

    @Schema(description = "班级排名")
    private Integer classRank;

    @Schema(description = "总排名")
    private Integer overallRank;
}
