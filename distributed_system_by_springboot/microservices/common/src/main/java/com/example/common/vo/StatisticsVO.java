package com.example.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计数据视图对象
 */
@Data
@Schema(description = "统计数据视图对象")
public class StatisticsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "学生总数")
    private Long studentCount;

    @Schema(description = "教师总数")
    private Long teacherCount;

    @Schema(description = "课程总数")
    private Long courseCount;

    @Schema(description = "教学班总数")
    private Long teachingClassCount;

    @Schema(description = "选课总数")
    private Long enrollmentCount;

    @Schema(description = "成绩记录总数")
    private Long scoreCount;

    @Schema(description = "平均分")
    private BigDecimal averageScore;

    @Schema(description = "最高分")
    private BigDecimal maxScore;

    @Schema(description = "最低分")
    private BigDecimal minScore;

    @Schema(description = "及格率")
    private BigDecimal passRate;

    @Schema(description = "优秀率")
    private BigDecimal excellentRate;
}
