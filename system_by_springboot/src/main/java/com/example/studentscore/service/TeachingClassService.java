package com.example.studentscore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.TeachingClassDTO;
import com.example.studentscore.entity.TeachingClass;
import com.example.studentscore.query.TeachingClassQuery;
import com.example.studentscore.vo.TeachingClassVO;

import java.util.List;

/**
 * 教学班服务接口
 *
 * @author system
 */
public interface TeachingClassService extends IService<TeachingClass> {

    /**
     * 分页查询教学班
     */
    IPage<TeachingClassVO> pageTeachingClasses(TeachingClassQuery query);

    /**
     * 根据教学班号查询
     */
    TeachingClass getByClassId(String classId);

    /**
     * 获取教学班详情
     */
    TeachingClassVO getTeachingClassDetail(Long id);

    /**
     * 添加教学班
     */
    boolean addTeachingClass(TeachingClassDTO dto);

    /**
     * 更新教学班
     */
    boolean updateTeachingClass(TeachingClassDTO dto);

    /**
     * 获取教师的教学班列表
     */
    List<TeachingClassVO> getTeacherClasses(Long teacherDbId);

    /**
     * 更新教学班人数
     */
    void updateClassSize(Long id);

    /**
     * 获取教学班学生列表（带成绩）
     */
    List<Object> getClassStudentsWithScores(Long teachingClassDbId);

    /**
     * 获取所有教学班列表（带关联信息）
     */
    List<TeachingClassVO> listAllTeachingClasses();
}
