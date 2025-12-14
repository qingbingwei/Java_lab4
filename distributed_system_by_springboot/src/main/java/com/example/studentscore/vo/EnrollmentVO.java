package com.example.studentscore.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课视图对象
 *
 * @author system
 */
@Data
@Schema(description = "选课视图对象")
public class EnrollmentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "选课ID")
    private Long id;

    @Schema(description = "选课记录ID")
    private Long enrollmentId;

    @Schema(description = "学生数据库ID")
    private Long studentDbId;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "教学班数据库ID")
    private Long teachingClassDbId;

    @Schema(description = "教学班号")
    private String classId;

    @Schema(description = "教学班名称")
    private String className;

    @Schema(description = "课程数据库ID")
    private Long courseDbId;

    @Schema(description = "课程代码")
    private String courseCode;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程类型")
    private String courseType;

    @Schema(description = "学分")
    private BigDecimal credit;

    @Schema(description = "教师数据库ID")
    private Long teacherDbId;

    @Schema(description = "教师工号")
    private String teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "学期")
    private String semester;

    @Schema(description = "上课时间")
    private String schedule;

    @Schema(description = "上课地点")
    private String classroom;

    @Schema(description = "选课时间")
    private LocalDateTime enrollTime;

    @Schema(description = "选课状态")
    private String status;
}
