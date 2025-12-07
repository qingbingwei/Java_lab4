package com.example.studentscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 成绩DTO
 *
 * @author system
 */
@Data
@Schema(description = "成绩数据传输对象")
public class ScoreDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "学生ID不能为空")
    @Schema(description = "学生ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long studentDbId;

    @NotNull(message = "教学班ID不能为空")
    @Schema(description = "教学班ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teachingClassDbId;

    @Min(value = 0, message = "平时成绩不能小于0")
    @Max(value = 100, message = "平时成绩不能大于100")
    @Schema(description = "平时成绩")
    private Integer regularScore;

    @Min(value = 0, message = "期中成绩不能小于0")
    @Max(value = 100, message = "期中成绩不能大于100")
    @Schema(description = "期中成绩")
    private Integer midtermScore;

    @Min(value = 0, message = "实验成绩不能小于0")
    @Max(value = 100, message = "实验成绩不能大于100")
    @Schema(description = "实验成绩")
    private Integer experimentScore;

    @Min(value = 0, message = "期末成绩不能小于0")
    @Max(value = 100, message = "期末成绩不能大于100")
    @Schema(description = "期末成绩")
    private Integer finalExamScore;
}
