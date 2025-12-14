package com.example.studentscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 教学班DTO
 *
 * @author system
 */
@Data
@Schema(description = "教学班数据传输对象")
public class TeachingClassDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "教学班号不能为空")
    @Schema(description = "教学班号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classId;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long courseDbId;

    @NotNull(message = "教师ID不能为空")
    @Schema(description = "教师ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teacherDbId;

    @NotBlank(message = "开课学期不能为空")
    @Schema(description = "开课学期", requiredMode = Schema.RequiredMode.REQUIRED)
    private String semester;

    @NotNull(message = "容量不能为空")
    @Min(value = 1, message = "容量必须大于0")
    @Schema(description = "容量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer capacity;

    @Schema(description = "上课时间")
    private String scheduleTime;

    @Schema(description = "上课地点")
    private String classroom;
}
