package com.example.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生实体类
 *
 * @author system
 */
@Data
@TableName("student")
@Schema(description = "学生实体")
public class Student implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.NONE)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "学号", example = "2023010001")
    private String studentId;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "性别", example = "MALE")
    private String gender;

    @Schema(description = "班级", example = "计算机2301")
    private String className;

    @Schema(description = "年级", example = "2023")
    private String grade;

    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标志", hidden = true)
    private Integer deleted;
}
