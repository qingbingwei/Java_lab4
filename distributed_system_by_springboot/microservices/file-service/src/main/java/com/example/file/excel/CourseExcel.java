package com.example.file.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 课程Excel数据模型
 */
@Data
public class CourseExcel {
    
    @ExcelProperty("课程号")
    private String courseId;
    
    @ExcelProperty("课程名称")
    private String name;
    
    @ExcelProperty("学分")
    private Double credits;
    
    @ExcelProperty("学时")
    private Integer hours;
    
    @ExcelProperty("课程类型")
    private String type;
    
    @ExcelProperty("课程描述")
    private String description;
}
