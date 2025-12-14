package com.example.studentscore.controller;

import com.example.studentscore.common.Result;
import com.example.studentscore.dto.FileDTO;
import com.example.studentscore.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件上传下载控制器
 */
@Tag(name = "文件管理", description = "文件上传下载接口")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传单个文件")
    @PostMapping("/upload")
    public Result<FileDTO> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件分类") @RequestParam(value = "category", required = false) String category) {
        FileDTO fileDTO = fileService.uploadFile(file, category);
        return Result.success("文件上传成功", fileDTO);
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public Result<List<FileDTO>> uploadFiles(
            @Parameter(description = "文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "文件分类") @RequestParam(value = "category", required = false) String category) {
        List<FileDTO> fileDTOs = fileService.uploadFiles(files, category);
        return Result.success("成功上传 " + fileDTOs.size() + " 个文件", fileDTOs);
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download/{fileId}")
    public void downloadFile(
            @Parameter(description = "文件ID") @PathVariable String fileId,
            HttpServletResponse response) {
        fileService.downloadFile(fileId, response);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileId}")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        boolean result = fileService.deleteFile(fileId);
        return result ? Result.success("文件删除成功", true) : Result.error("文件不存在");
    }

    @Operation(summary = "获取文件信息")
    @GetMapping("/{fileId}")
    public Result<FileDTO> getFileInfo(
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        FileDTO fileDTO = fileService.getFileInfo(fileId);
        if (fileDTO == null) {
            return Result.error("文件不存在");
        }
        return Result.success(fileDTO);
    }

    @Operation(summary = "获取文件列表")
    @GetMapping("/list")
    public Result<List<FileDTO>> listFiles(
            @Parameter(description = "文件分类") @RequestParam(value = "category", required = false) String category) {
        List<FileDTO> files = fileService.listFiles(category);
        return Result.success(files);
    }
}
