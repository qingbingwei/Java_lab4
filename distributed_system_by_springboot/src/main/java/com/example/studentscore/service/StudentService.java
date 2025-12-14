package com.example.studentscore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.StudentDTO;
import com.example.studentscore.entity.Student;
import com.example.studentscore.query.StudentQuery;
import com.example.studentscore.vo.StudentScoreDetailVO;

import java.util.List;

/**
 * 学生服务接口
 *
 * @author system
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生
     */
    IPage<Student> pageStudents(StudentQuery query);

    /**
     * 根据学号查询学生
     */
    Student getByStudentId(String studentId);

    /**
     * 添加学生
     */
    boolean addStudent(StudentDTO dto);

    /**
     * 更新学生
     */
    boolean updateStudent(StudentDTO dto);

    /**
     * 批量添加学生
     */
    boolean batchAddStudents(List<StudentDTO> dtoList);

    /**
     * 获取学生成绩详情
     */
    StudentScoreDetailVO getStudentScoreDetail(Long id);

    /**
     * 获取学生成绩详情（通过学号）
     */
    StudentScoreDetailVO getStudentScoreDetailByStudentId(String studentId);
}
