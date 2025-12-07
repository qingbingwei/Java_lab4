package com.example.studentscore.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 教师Excel导入导出模型
 */
@Data
@HeadRowHeight(20)
@ColumnWidth(15)
public class TeacherExcel {

    @ExcelProperty("教师编号")
    @ColumnWidth(15)
    private String teacherId;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("职称")
    private String title;

    @ExcelProperty("所属学院")
    @ColumnWidth(20)
    private String department;

    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("邮箱")
    @ColumnWidth(25)
    private String email;
}
