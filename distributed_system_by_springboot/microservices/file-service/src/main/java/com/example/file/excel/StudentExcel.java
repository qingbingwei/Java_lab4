package com.example.file.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 学生Excel数据模型
 */
@Data
public class StudentExcel {
    
    @ExcelProperty("学号")
    private String studentId;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("性别")
    private String gender;
    
    @ExcelProperty("专业")
    private String major;
    
    @ExcelProperty("班级")
    private String className;
    
    @ExcelProperty("年级")
    private Integer grade;
    
    @ExcelProperty("电话")
    private String phone;
    
    @ExcelProperty("邮箱")
    private String email;
}
