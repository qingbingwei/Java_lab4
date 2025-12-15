package com.example.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 班级统计视图对象
 */
@Data
@Schema(description = "班级统计视图对象")
public class ClassStatisticsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "学生人数")
    private Long studentCount;

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
