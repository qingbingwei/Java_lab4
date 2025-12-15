package com.example.file.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 教师Excel数据模型
 */
@Data
public class TeacherExcel {
    
    @ExcelProperty("工号")
    private String teacherId;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("部门")
    private String department;
    
    @ExcelProperty("职称")
    private String title;
    
    @ExcelProperty("电话")
    private String phone;
    
    @ExcelProperty("邮箱")
    private String email;
}
