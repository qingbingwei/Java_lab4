package com.example.common.entity;

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

    @TableId(value = "id", type = IdType.NONE)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "教学班号", example = "C001-01")
    private String classId;

    @Schema(description = "课程ID")
    private Long courseDbId;

    @Schema(description = "教师ID")
    private Long teacherDbId;

    @Schema(description = "开课学期", example = "2023-2024-1")
    private String semester;

    @Schema(description = "容量", example = "50")
    private Integer capacity;

    @TableField(exist = false)
    @Schema(description = "描述")
    private String description;

    @Schema(description = "当前人数", example = "30")
    private Integer currentSize;

    @Schema(description = "上课时间", example = "周一 1-2节")
    private String scheduleTime;

    @Schema(description = "上课地点", example = "教学楼A101")
    private String classroom;

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
