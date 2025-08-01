package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.exception.FileStorageException;
import com.primehub.primecardadmin.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Value("${file.max-size:10MB}")
    private String maxFileSize;
    
    @Value("${file.allowed-types:image/jpeg,image/png,image/gif,application/pdf}")
    private String allowedFileTypes;
    
    private Path fileStorageLocation;
    private long maxFileSizeInBytes;
    private String[] allowedTypes;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSizeInBytes = parseSize(maxFileSize);
        this.allowedTypes = allowedFileTypes.split(",");
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("无法创建文件上传目录", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new FileStorageException("无法存储空文件");
        }
        
        // 检查文件大小
        if (file.getSize() > maxFileSizeInBytes) {
            throw new FileStorageException("文件大小超过限制: " + maxFileSize);
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        boolean isAllowedType = false;
        
        if (contentType != null) {
            for (String type : allowedTypes) {
                if (contentType.equals(type.trim())) {
                    isAllowedType = true;
                    break;
                }
            }
        }
        
        if (!isAllowedType) {
            throw new FileStorageException("不支持的文件类型: " + contentType);
        }
        
        // 获取文件名
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        
        // 检查文件名是否包含无效字符
        if (originalFilename.contains("..")) {
            throw new FileStorageException("文件名包含无效路径序列: " + originalFilename);
        }
        
        // 生成唯一文件名
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        try {
            // 复制文件到目标位置
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return newFilename;
        } catch (IOException ex) {
            throw new FileStorageException("无法存储文件 " + originalFilename, ex);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("无法删除文件 " + filename, ex);
        }
    }

    @Override
    public String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // 没有扩展名
        }
        return filename.substring(lastDotIndex);
    }

    @Override
    public long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.isEmpty()) {
            return 0;
        }
        
        Pattern pattern = Pattern.compile("(\\d+)([KMG]B?)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sizeStr.trim());
        
        if (matcher.matches()) {
            long size = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            
            if (unit == null) {
                return size; // 字节
            }
            
            switch (unit.toUpperCase()) {
                case "KB":
                case "K":
                    return size * 1024;
                case "MB":
                case "M":
                    return size * 1024 * 1024;
                case "GB":
                case "G":
                    return size * 1024 * 1024 * 1024;
                default:
                    return size;
            }
        }
        
        return 0;
    }
}