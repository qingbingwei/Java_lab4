package com.example.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生选课实体类
 *
 * @author system
 */
@Data
@TableName("enrollment")
@Schema(description = "学生选课实体")
public class Enrollment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.NONE)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "学生ID")
    private Long studentDbId;

    @Schema(description = "教学班ID")
    private Long teachingClassDbId;

    @Schema(description = "选课时间")
    private LocalDateTime enrollTime;

    @TableField(exist = false)
    @Schema(description = "选课状态", example = "ENROLLED")
    private String status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "删除标志", hidden = true)
    private Integer deleted;

    @TableField(exist = false)
    @Schema(description = "学生信息")
    private Student student;

    @TableField(exist = false)
    @Schema(description = "教学班信息")
    private TeachingClass teachingClass;
}
