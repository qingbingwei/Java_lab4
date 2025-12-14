package com.example.studentscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 选课DTO
 *
 * @author system
 */
@Data
@Schema(description = "选课数据传输对象")
public class EnrollmentDTO implements Serializable {

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
}
