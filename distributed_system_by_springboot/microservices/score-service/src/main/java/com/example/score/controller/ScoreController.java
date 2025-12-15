package com.example.score.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.dto.ScoreDTO;
import com.example.common.query.ScoreQuery;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.common.vo.ScoreVO;
import com.example.common.vo.StudentScoreDetailVO;
import com.example.score.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成绩控制器
 */
@Tag(name = "成绩管理", description = "成绩信息管理相关接口")
@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {
    
    private final ScoreService scoreService;
    
    @Operation(summary = "分页查询成绩")
    @GetMapping("/page")
    public Result<PageResult<ScoreVO>> page(ScoreQuery query) {
        IPage<ScoreVO> page = scoreService.page(query);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
    
    @Operation(summary = "获取所有成绩")
    @GetMapping("/list")
    public Result<List<ScoreVO>> list() {
        return Result.success(scoreService.list());
    }
    
    @Operation(summary = "根据ID获取成绩")
    @GetMapping("/{id}")
    public Result<ScoreVO> getById(@Parameter(description = "成绩ID") @PathVariable("id") String id) {
        ScoreVO vo = scoreService.getById(id);
        return vo != null ? Result.success(vo) : Result.error("成绩记录不存在");
    }
    
    @Operation(summary = "根据学号获取成绩列表")
    @GetMapping("/student/{studentId}")
    public Result<List<ScoreVO>> getByStudentId(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        return Result.success(scoreService.getByStudentId(studentId));
    }
    
    @Operation(summary = "根据教学班获取成绩列表")
    @GetMapping("/class/{classId}")
    public Result<List<ScoreVO>> getByClassId(@Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        return Result.success(scoreService.getByClassId(classId));
    }
    
    @Operation(summary = "获取学生成绩详情")
    @GetMapping("/student/{studentId}/detail")
    public StudentScoreDetailVO getStudentScoreDetail(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        return scoreService.getStudentScoreDetail(studentId);
    }
    
    @Operation(summary = "新增成绩")
    @PostMapping
    public Result<Boolean> save(@RequestBody ScoreDTO dto) {
        return Result.success(scoreService.save(dto));
    }
    
    @Operation(summary = "批量新增成绩")
    @PostMapping("/batch")
    public Result<Boolean> saveBatch(@RequestBody List<ScoreDTO> dtos) {
        return Result.success(scoreService.saveBatch(dtos));
    }
    
    @Operation(summary = "更新成绩")
    @PutMapping
    public Result<Boolean> update(@RequestBody ScoreDTO dto) {
        return Result.success(scoreService.update(dto));
    }
    
    @Operation(summary = "删除成绩")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "成绩ID") @PathVariable("id") String id) {
        return Result.success(scoreService.delete(id));
    }
    
    @Operation(summary = "批量删除成绩")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(scoreService.deleteBatch(ids));
    }
    
    @Operation(summary = "检查成绩是否存在")
    @GetMapping("/exists")
    public Result<Boolean> exists(
            @Parameter(description = "学号") @RequestParam("studentId") String studentId,
            @Parameter(description = "教学班编号") @RequestParam("classId") String classId) {
        return Result.success(scoreService.exists(studentId, classId));
    }
    
    @Operation(summary = "统计成绩总数")
    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(scoreService.count());
    }
    
    @Operation(summary = "获取成绩排名")
    @GetMapping("/ranking")
    public Result<List<ScoreVO>> getRankingAll(
            @Parameter(description = "教学班数据库ID") @RequestParam(required = false) Long teachingClassDbId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "教师数据库ID（教师只看自己教学班的成绩排名）") @RequestParam(required = false) Long teacherDbId) {
        return Result.success(scoreService.getRankingAll(teachingClassDbId, semester, teacherDbId));
    }
    
    @Operation(summary = "获取班级成绩排名")
    @GetMapping("/ranking/{classId}")
    public Result<List<ScoreVO>> getRanking(@Parameter(description = "教学班编号") @PathVariable("classId") String classId) {
        return Result.success(scoreService.getRanking(classId));
    }
    
    @Operation(summary = "获取学生综合排名")
    @GetMapping("/student/{studentId}/ranking")
    public Result<List<ScoreVO>> getStudentRanking(@Parameter(description = "学号") @PathVariable("studentId") String studentId) {
        return Result.success(scoreService.getStudentRanking(studentId));
    }
}
