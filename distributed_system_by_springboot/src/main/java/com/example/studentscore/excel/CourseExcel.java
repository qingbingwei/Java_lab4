package com.example.studentscore.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 课程Excel导入导出模型
 */
@Data
@HeadRowHeight(20)
@ColumnWidth(15)
public class CourseExcel {

    @ExcelProperty("课程编号")
    @ColumnWidth(15)
    private String courseId;

    @ExcelProperty("课程名称")
    @ColumnWidth(20)
    private String courseName;

    @ExcelProperty("学分")
    @ColumnWidth(10)
    private Integer credits;

    @ExcelProperty("课程类型")
    private String courseType;

    @ExcelProperty("学时")
    @ColumnWidth(10)
    private Integer hours;

    @ExcelProperty("课程描述")
    @ColumnWidth(40)
    private String description;
}
