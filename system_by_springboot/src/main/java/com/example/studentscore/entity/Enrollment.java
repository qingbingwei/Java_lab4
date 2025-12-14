package com.example.studentscore.entity;

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

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.NONE)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Long studentDbId;

    /**
     * 教学班ID
     */
    @Schema(description = "教学班ID")
    private Long teachingClassDbId;

    /**
     * 选课时间
     */
    @Schema(description = "选课时间")
    private LocalDateTime enrollTime;

    /**
     * 选课状态: ENROLLED-已选, DROPPED-已退
     * 注意：数据库中没有此字段，通过deleted字段判断
     */
    @TableField(exist = false)
    @Schema(description = "选课状态", example = "ENROLLED")
    private String status;

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

    /**
     * 学生信息（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "学生信息")
    private Student student;

    /**
     * 教学班信息（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教学班信息")
    private TeachingClass teachingClass;
}
