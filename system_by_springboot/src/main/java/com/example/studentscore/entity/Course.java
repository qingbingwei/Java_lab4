package com.example.studentscore.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体类
 *
 * @author system
 */
@Data
@TableName("course")
@Schema(description = "课程实体")
public class Course implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.NONE)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 课程编号
     */
    @Schema(description = "课程编号", example = "C001")
    private String courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "高等数学")
    private String courseName;

    /**
     * 学分
     */
    @Schema(description = "学分", example = "4")
    private Integer credits;

    /**
     * 课程类型: REQUIRED-必修, ELECTIVE-选修
     */
    @Schema(description = "课程类型", example = "REQUIRED")
    private String courseType;

    /**
     * 学时
     */
    @Schema(description = "学时", example = "64")
    private Integer hours;

    /**
     * 课程描述
     */
    @Schema(description = "课程描述")
    private String description;

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
