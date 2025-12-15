package com.example.file.controller;

import com.example.common.dto.FileDTO;
import com.example.common.result.Result;
import com.example.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件控制器
 */
@Tag(name = "文件管理", description = "文件上传下载相关接口")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileDTO> upload(@Parameter(description = "文件") @RequestParam("file") MultipartFile file) {
        return Result.success(fileService.upload(file));
    }
    
    @Operation(summary = "下载文件")
    @GetMapping("/download/{filename}")
    public void download(
            @Parameter(description = "文件名") @PathVariable("filename") String filename,
            HttpServletResponse response) {
        fileService.download(filename, response);
    }
    
    @Operation(summary = "删除文件")
    @DeleteMapping("/{filename}")
    public Result<Boolean> delete(@Parameter(description = "文件名") @PathVariable("filename") String filename) {
        return Result.success(fileService.delete(filename));
    }
    
    @Operation(summary = "获取文件列表")
    @GetMapping("/list")
    public Result<List<FileDTO>> list() {
        return Result.success(fileService.list());
    }
}
