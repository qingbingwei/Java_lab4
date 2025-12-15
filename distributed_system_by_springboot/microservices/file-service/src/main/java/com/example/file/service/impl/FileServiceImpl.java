package com.example.file.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.example.common.dto.FileDTO;
import com.example.common.exception.BusinessException;
import com.example.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {
    
    @Value("${file.upload-path:../uploads/}")
    private String uploadPath;
    
    @Override
    public FileDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = IdUtil.simpleUUID() + extension;
            
            // 保存文件
            File destFile = new File(uploadDir, newFilename);
            file.transferTo(destFile);
            
            // 返回文件信息
            FileDTO dto = new FileDTO();
            dto.setStoredName(newFilename);
            dto.setOriginalName(originalFilename);
            dto.setSize(file.getSize());
            dto.setContentType(file.getContentType());
            dto.setUploadTime(LocalDateTime.now());
            dto.setDownloadUrl("/files/download/" + newFilename);
            
            return dto;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public void download(String filename, HttpServletResponse response) {
        File file = new File(uploadPath, filename);
        if (!file.exists()) {
            throw new BusinessException("文件不存在");
        }
        
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + 
                    URLEncoder.encode(filename, StandardCharsets.UTF_8));
            response.setContentLengthLong(file.length());
            
            try (OutputStream out = response.getOutputStream()) {
                Files.copy(file.toPath(), out);
            }
        } catch (IOException e) {
            log.error("文件下载失败", e);
            throw new BusinessException("文件下载失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean delete(String filename) {
        File file = new File(uploadPath, filename);
        if (!file.exists()) {
            throw new BusinessException("文件不存在");
        }
        
        return FileUtil.del(file);
    }
    
    @Override
    public List<FileDTO> list() {
        List<FileDTO> result = new ArrayList<>();
        File uploadDir = new File(uploadPath);
        
        if (uploadDir.exists() && uploadDir.isDirectory()) {
            File[] files = uploadDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        FileDTO dto = new FileDTO();
                        dto.setStoredName(file.getName());
                        dto.setOriginalName(file.getName());
                        dto.setSize(file.length());
                        dto.setUploadTime(LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(file.lastModified()),
                                java.time.ZoneId.systemDefault()));
                        dto.setDownloadUrl("/files/download/" + file.getName());
                        result.add(dto);
                    }
                }
            }
        }
        
        return result;
    }
}
