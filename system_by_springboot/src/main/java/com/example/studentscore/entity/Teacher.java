package com.example.studentscore.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师实体类
 *
 * @author system
 */
@Data
@TableName("teacher")
@Schema(description = "教师实体")
public class Teacher implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 教师编号
     */
    @Schema(description = "教师编号", example = "T0001")
    private String teacherId;

    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "李教授")
    private String name;

    /**
     * 职称
     */
    @Schema(description = "职称", example = "教授")
    private String title;

    /**
     * 所属学院
     */
    @Schema(description = "所属学院", example = "计算机学院")
    private String department;

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
