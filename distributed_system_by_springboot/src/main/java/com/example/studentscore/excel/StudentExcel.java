package com.example.studentscore.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 学生Excel导入导出模型
 */
@Data
@HeadRowHeight(20)
@ColumnWidth(15)
public class StudentExcel {

    @ExcelProperty("学号")
    @ColumnWidth(20)
    private String studentId;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    @ColumnWidth(10)
    private String gender;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("年级")
    @ColumnWidth(10)
    private String grade;

    @ExcelProperty("专业")
    @ColumnWidth(25)
    private String major;

    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("邮箱")
    @ColumnWidth(25)
    private String email;
}
