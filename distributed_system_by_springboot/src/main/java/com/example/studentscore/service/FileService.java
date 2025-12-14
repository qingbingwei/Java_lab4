package com.example.studentscore.service;

import com.example.studentscore.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传单个文件
     * @param file 文件
     * @param category 文件分类（可选）
     * @return 文件信息
     */
    FileDTO uploadFile(MultipartFile file, String category);

    /**
     * 批量上传文件
     * @param files 文件列表
     * @param category 文件分类（可选）
     * @return 文件信息列表
     */
    List<FileDTO> uploadFiles(MultipartFile[] files, String category);

    /**
     * 下载文件
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void downloadFile(String fileId, HttpServletResponse response);

    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(String fileId);

    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileDTO getFileInfo(String fileId);

    /**
     * 获取文件列表
     * @param category 文件分类
     * @return 文件列表
     */
    List<FileDTO> listFiles(String category);
}
