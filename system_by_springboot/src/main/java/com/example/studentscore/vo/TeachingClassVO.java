package com.example.studentscore.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学班视图对象
 *
 * @author system
 */
@Data
@Schema(description = "教学班视图对象")
public class TeachingClassVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "教学班号")
    private String classId;

    @Schema(description = "课程编号")
    private String courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "学分")
    private Integer credits;

    @Schema(description = "教师编号")
    private String teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "开课学期")
    private String semester;

    @Schema(description = "容量")
    private Integer capacity;

    @Schema(description = "当前人数")
    private Integer currentSize;

    @Schema(description = "上课时间")
    private String scheduleTime;

    @Schema(description = "上课地点")
    private String classroom;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
