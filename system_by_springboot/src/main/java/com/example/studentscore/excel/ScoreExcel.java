package com.example.studentscore.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 成绩Excel导入导出模型
 */
@Data
@HeadRowHeight(20)
@ColumnWidth(15)
public class ScoreExcel {

    @ExcelProperty("学号")
    @ColumnWidth(20)
    private String studentId;

    @ExcelProperty("学生姓名")
    private String studentName;

    @ExcelProperty("教学班号")
    @ColumnWidth(15)
    private String classId;

    @ExcelProperty("课程名称")
    @ColumnWidth(20)
    private String courseName;

    @ExcelProperty("教师姓名")
    private String teacherName;

    @ExcelProperty("学期")
    private String semester;

    @ExcelProperty("平时成绩")
    @ColumnWidth(12)
    private Integer regularScore;

    @ExcelProperty("期中成绩")
    @ColumnWidth(12)
    private Integer midtermScore;

    @ExcelProperty("实验成绩")
    @ColumnWidth(12)
    private Integer experimentScore;

    @ExcelProperty("期末成绩")
    @ColumnWidth(12)
    private Integer finalExamScore;

    @ExcelProperty("综合成绩")
    @ColumnWidth(12)
    private Double finalScore;
}
