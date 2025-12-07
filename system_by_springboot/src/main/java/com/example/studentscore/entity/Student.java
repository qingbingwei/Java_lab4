package com.example.studentscore.entity;

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

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 学号
     */
    @Schema(description = "学号", example = "2023010001")
    private String studentId;

    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张三")
    private String name;

    /**
     * 性别: MALE-男, FEMALE-女
     */
    @Schema(description = "性别", example = "MALE")
    private String gender;

    /**
     * 班级
     */
    @Schema(description = "班级", example = "计算机2301")
    private String className;

    /**
     * 年级
     */
    @Schema(description = "年级", example = "2023")
    private String grade;

    /**
     * 专业
     */
    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @Schema(description = "删除标志", hidden = true)
    private Integer deleted;
}
