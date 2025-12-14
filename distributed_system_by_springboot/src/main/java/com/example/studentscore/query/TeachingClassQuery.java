package com.example.studentscore.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 教学班查询参数
 *
 * @author system
 */
@Data
@Schema(description = "教学班查询参数")
public class TeachingClassQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "教学班号")
    private String classId;

    @Schema(description = "课程编号")
    private String courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "教师编号")
    private String teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "关键词")
    private String keyword;
}
