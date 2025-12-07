package com.example.studentscore.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 成绩查询参数
 *
 * @author system
 */
@Data
@Schema(description = "成绩查询参数")
public class ScoreQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "学生数据库ID（用于学生只查自己的成绩）")
    private Long studentDbId;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "课程编号")
    private String courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "教学班号")
    private String classId;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "最低分")
    private Integer minScore;

    @Schema(description = "最高分")
    private Integer maxScore;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "排序方式（asc/desc）")
    private String orderType;
}
