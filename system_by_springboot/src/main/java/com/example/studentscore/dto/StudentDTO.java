package com.example.studentscore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 学生DTO
 *
 * @author system
 */
@Data
@Schema(description = "学生数据传输对象")
public class StudentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "学号不能为空")
    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String studentId;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "性别必须是MALE或FEMALE")
    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;
}
