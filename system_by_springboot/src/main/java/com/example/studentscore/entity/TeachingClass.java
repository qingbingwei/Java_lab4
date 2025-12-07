package com.example.studentscore.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学班实体类
 *
 * @author system
 */
@Data
@TableName("teaching_class")
@Schema(description = "教学班实体")
public class TeachingClass implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 教学班号
     */
    @Schema(description = "教学班号", example = "C001-01")
    private String classId;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseDbId;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    private Long teacherDbId;

    /**
     * 开课学期
     */
    @Schema(description = "开课学期", example = "2023-2024-1")
    private String semester;

    /**
     * 容量
     */
    @Schema(description = "容量", example = "50")
    private Integer capacity;

    /**
     * 描述（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "描述")
    private String description;

    /**
     * 当前人数
     */
    @Schema(description = "当前人数", example = "30")
    private Integer currentSize;

    /**
     * 上课时间
     */
    @Schema(description = "上课时间", example = "周一 1-2节")
    private String scheduleTime;

    /**
     * 上课地点
     */
    @Schema(description = "上课地点", example = "教学楼A101")
    private String classroom;

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
     * 课程信息（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "课程信息")
    private Course course;

    /**
     * 教师信息（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "教师信息")
    private Teacher teacher;
}
