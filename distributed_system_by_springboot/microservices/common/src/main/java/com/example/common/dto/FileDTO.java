package com.example.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息DTO
 */
@Data
@Schema(description = "文件信息")
public class FileDTO {

    @Schema(description = "文件ID")
    private String fileId;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "存储文件名")
    private String storedName;

    @Schema(description = "文件大小(字节)")
    private Long size;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "文件路径")
    private String path;

    @Schema(description = "下载URL")
    private String downloadUrl;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;
}
