package com.example.studentscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 课程DTO
 *
 * @author system
 */
@Data
@Schema(description = "课程数据传输对象")
public class CourseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "课程编号不能为空")
    @Schema(description = "课程编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courseId;

    @NotBlank(message = "课程名称不能为空")
    @Schema(description = "课程名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courseName;

    @NotNull(message = "学分不能为空")
    @Min(value = 1, message = "学分必须大于0")
    @Schema(description = "学分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer credits;

    @Schema(description = "课程类型")
    private String courseType;

    @Schema(description = "学时")
    private Integer hours;

    @Schema(description = "课程描述")
    private String description;
}
