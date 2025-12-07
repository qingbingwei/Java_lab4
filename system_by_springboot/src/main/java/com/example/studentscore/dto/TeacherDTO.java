package com.example.studentscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 教师DTO
 *
 * @author system
 */
@Data
@Schema(description = "教师数据传输对象")
public class TeacherDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "教师编号不能为空")
    @Schema(description = "教师编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String teacherId;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "所属学院")
    private String department;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;
}
