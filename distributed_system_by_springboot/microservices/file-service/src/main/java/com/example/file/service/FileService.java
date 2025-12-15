package com.example.file.service;

import com.example.common.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     */
    FileDTO upload(MultipartFile file);
    
    /**
     * 下载文件
     */
    void download(String filename, HttpServletResponse response);
    
    /**
     * 删除文件
     */
    boolean delete(String filename);
    
    /**
     * 获取文件列表
     */
    List<FileDTO> list();
}
