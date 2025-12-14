package com.example.studentscore.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.studentscore.dto.FileDTO;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar}")
    private String allowedTypes;

    /**
     * 文件元数据存储（实际项目中应存储到数据库）
     */
    private final Map<String, FileDTO> fileMetaStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                log.info("创建上传目录: {}", uploadDir.getAbsolutePath());
            }
        }
    }

    @Override
    public FileDTO uploadFile(MultipartFile file, String category) {
        validateFile(file);

        try {
            // 生成文件ID
            String fileId = IdUtil.fastSimpleUUID();
            
            // 获取原始文件名和扩展名
            String originalName = file.getOriginalFilename();
            String extension = FileUtil.extName(originalName);
            
            // 生成存储文件名
            String storedName = fileId + "." + extension;
            
            // 生成存储路径（按日期分目录）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String categoryPath = StrUtil.isNotBlank(category) ? category : "default";
            String relativePath = categoryPath + "/" + datePath;
            
            // 创建目录
            Path targetDir = Paths.get(uploadPath, relativePath);
            Files.createDirectories(targetDir);
            
            // 保存文件
            Path targetPath = targetDir.resolve(storedName);
            file.transferTo(targetPath.toFile());
            
            // 构建文件信息
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFileId(fileId);
            fileDTO.setOriginalName(originalName);
            fileDTO.setStoredName(storedName);
            fileDTO.setSize(file.getSize());
            fileDTO.setContentType(file.getContentType());
            fileDTO.setPath(relativePath + "/" + storedName);
            fileDTO.setDownloadUrl("/api/files/download/" + fileId);
            fileDTO.setUploadTime(LocalDateTime.now());
            
            // 保存元数据
            fileMetaStore.put(fileId, fileDTO);
            
            log.info("文件上传成功: {} -> {}", originalName, targetPath);
            return fileDTO;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public List<FileDTO> uploadFiles(MultipartFile[] files, String category) {
        if (files == null || files.length == 0) {
            throw new BusinessException("没有选择要上传的文件");
        }
        
        return Arrays.stream(files)
                .map(file -> uploadFile(file, category))
                .collect(Collectors.toList());
    }

    @Override
    public void downloadFile(String fileId, HttpServletResponse response) {
        FileDTO fileDTO = getFileInfo(fileId);
        if (fileDTO == null) {
            throw new BusinessException("文件不存在");
        }
        
        Path filePath = Paths.get(uploadPath, fileDTO.getPath());
        File file = filePath.toFile();
        
        if (!file.exists()) {
            throw new BusinessException("文件不存在或已被删除");
        }
        
        try {
            // 设置响应头
            response.setContentType(fileDTO.getContentType());
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileDTO.getOriginalName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedFileName);
            response.setContentLengthLong(file.length());
            
            // 写入文件内容
            try (OutputStream os = response.getOutputStream()) {
                Files.copy(filePath, os);
                os.flush();
            }
            
            log.info("文件下载成功: {}", fileDTO.getOriginalName());
            
        } catch (IOException e) {
            log.error("文件下载失败", e);
            throw new BusinessException("文件下载失败：" + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileId) {
        FileDTO fileDTO = fileMetaStore.get(fileId);
        if (fileDTO == null) {
            return false;
        }
        
        // 删除物理文件
        Path filePath = Paths.get(uploadPath, fileDTO.getPath());
        try {
            Files.deleteIfExists(filePath);
            fileMetaStore.remove(fileId);
            log.info("文件删除成功: {}", fileDTO.getOriginalName());
            return true;
        } catch (IOException e) {
            log.error("文件删除失败", e);
            throw new BusinessException("文件删除失败：" + e.getMessage());
        }
    }

    @Override
    public FileDTO getFileInfo(String fileId) {
        return fileMetaStore.get(fileId);
    }

    @Override
    public List<FileDTO> listFiles(String category) {
        return fileMetaStore.values().stream()
                .filter(dto -> {
                    if (StrUtil.isBlank(category)) {
                        return true;
                    }
                    return dto.getPath().startsWith(category + "/");
                })
                .sorted(Comparator.comparing(FileDTO::getUploadTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小超过限制（最大 " + (maxFileSize / 1024 / 1024) + "MB）");
        }
        
        // 检查文件类型
        String originalName = file.getOriginalFilename();
        String extension = FileUtil.extName(originalName);
        if (StrUtil.isBlank(extension)) {
            throw new BusinessException("无法识别的文件类型");
        }
        
        Set<String> allowedSet = Arrays.stream(allowedTypes.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        if (!allowedSet.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型：" + extension);
        }
    }
}
