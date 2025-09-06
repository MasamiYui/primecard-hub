package com.primehub.primecardadmin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.primehub.primecardadmin.config.OssConfig;
import com.primehub.primecardadmin.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 阿里云OSS文件存储服务实现类
 * 
 * @author PrimeCardHub
 * @since 2024-09-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {
    
    private final OSS ossClient;
    private final OssConfig.OssProperties ossProperties;
    
    // 支持的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // 最大文件大小 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    @Override
    public String uploadFile(MultipartFile file, String pathPrefix) {
        try {
            // 验证文件
            validateFile(file);
            
            // 生成唯一文件名
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            
            // 构建完整的对象键 (key)
            String objectKey = buildObjectKey(pathPrefix, fileName);
            
            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossProperties.getBucketName(),
                objectKey,
                file.getInputStream()
            );
            
            // 执行上传
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            
            // 构建访问URL
            String fileUrl = ossProperties.getUrlPrefix() + objectKey;
            
            log.info("文件上传成功: {} -> {}", file.getOriginalFilename(), fileUrl);
            return fileUrl;
            
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        try {
            if (!StringUtils.hasText(fileUrl)) {
                return;
            }
            
            // 从URL中提取对象键
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            if (StringUtils.hasText(objectKey)) {
                ossClient.deleteObject(ossProperties.getBucketName(), objectKey);
                log.info("文件删除成功: {}", fileUrl);
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }
    
    @Override
    public String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }
    
    @Override
    public boolean isValidImageType(MultipartFile file) {
        return file != null && ALLOWED_IMAGE_TYPES.contains(file.getContentType());
    }
    
    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        
        if (!isValidImageType(file)) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_IMAGE_TYPES));
        }
    }
    
    /**
     * 构建对象键
     */
    private String buildObjectKey(String pathPrefix, String fileName) {
        // 确保pathPrefix以/结尾
        if (StringUtils.hasText(pathPrefix) && !pathPrefix.endsWith("/")) {
            pathPrefix += "/";
        }
        
        // 添加日期分组
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        return (StringUtils.hasText(pathPrefix) ? pathPrefix : "") + datePrefix + "/" + fileName;
    }
    
    /**
     * 从URL中提取对象键
     */
    private String extractObjectKeyFromUrl(String fileUrl) {
        if (!StringUtils.hasText(fileUrl) || !fileUrl.startsWith(ossProperties.getUrlPrefix())) {
            return null;
        }
        return fileUrl.substring(ossProperties.getUrlPrefix().length());
    }
}