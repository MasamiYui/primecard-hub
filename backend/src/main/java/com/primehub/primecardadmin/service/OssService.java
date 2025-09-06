package com.primehub.primecardadmin.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云OSS文件存储服务接口
 * 
 * @author PrimeCardHub
 * @since 2024-09-06
 */
public interface OssService {
    
    /**
     * 上传文件到OSS
     * 
     * @param file 文件
     * @param pathPrefix 路径前缀（如：banner/, news/, avatar/）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String pathPrefix);
    
    /**
     * 删除OSS上的文件
     * 
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl);
    
    /**
     * 生成唯一文件名
     * 
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    String generateUniqueFileName(String originalFilename);
    
    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 扩展名
     */
    String getFileExtension(String filename);
    
    /**
     * 验证文件类型
     * 
     * @param file 文件
     * @return 是否为有效图片类型
     */
    boolean isValidImageType(MultipartFile file);
}