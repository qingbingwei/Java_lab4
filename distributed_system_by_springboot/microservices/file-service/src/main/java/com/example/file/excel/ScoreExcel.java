package com.example.file.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 成绩Excel数据模型
 */
@Data
public class ScoreExcel {
    
    @ExcelProperty("学号")
    private String studentId;
    
    @ExcelProperty("学生姓名")
    private String studentName;
    
    @ExcelProperty("教学班编号")
    private String classId;
    
    @ExcelProperty("课程名称")
    private String courseName;
    
    @ExcelProperty("平时成绩")
    private Double usualScore;
    
    @ExcelProperty("考试成绩")
    private Double examScore;
    
    @ExcelProperty("总成绩")
    private Double score;
}
