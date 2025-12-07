package com.example.studentscore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentscore.common.PageResult;
import com.example.studentscore.common.Result;
import com.example.studentscore.dto.ScoreDTO;
import com.example.studentscore.query.ScoreQuery;
import com.example.studentscore.service.ScoreService;
import com.example.studentscore.vo.CourseStatisticsVO;
import com.example.studentscore.vo.ScoreVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成绩管理控制器
 *
 * @author system
 */
@Tag(name = "成绩管理", description = "成绩的录入、查询、统计等接口")
@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @Operation(summary = "分页查询成绩列表")
    @GetMapping("/page")
    public Result<PageResult<ScoreVO>> pageScores(ScoreQuery query) {
        IPage<ScoreVO> page = scoreService.pageScores(query);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    @Operation(summary = "查询学生成绩列表")
    @GetMapping("/student/{studentDbId}")
    public Result<List<ScoreVO>> getStudentScores(
            @Parameter(description = "学生数据库ID") @PathVariable Long studentDbId) {
        return Result.success(scoreService.getStudentScores(studentDbId));
    }

    @Operation(summary = "获取成绩排名")
    @GetMapping("/ranking")
    public Result<List<ScoreVO>> getScoreRanking(ScoreQuery query) {
        return Result.success(scoreService.getScoreRanking(query));
    }

    @Operation(summary = "获取课程统计")
    @GetMapping("/statistics/course")
    public Result<List<CourseStatisticsVO>> getCourseStatistics(
            @Parameter(description = "学期") @RequestParam(required = false) String semester) {
        return Result.success(scoreService.getCourseStatistics(semester));
    }

    @Operation(summary = "录入成绩")
    @PostMapping
    public Result<Boolean> addScore(@Valid @RequestBody ScoreDTO dto) {
        return Result.success("录入成功", scoreService.addScore(dto));
    }

    @Operation(summary = "更新成绩")
    @PutMapping
    public Result<Boolean> updateScore(@Valid @RequestBody ScoreDTO dto) {
        return Result.success("更新成功", scoreService.updateScore(dto));
    }

    @Operation(summary = "删除成绩")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteScore(
            @Parameter(description = "成绩ID") @PathVariable Long id) {
        return Result.success("删除成功", scoreService.removeById(id));
    }

    @Operation(summary = "批量录入成绩")
    @PostMapping("/batch")
    public Result<Boolean> batchAddScores(@Valid @RequestBody List<ScoreDTO> dtoList) {
        return Result.success("批量录入成功", scoreService.batchAddScores(dtoList));
    }
}
