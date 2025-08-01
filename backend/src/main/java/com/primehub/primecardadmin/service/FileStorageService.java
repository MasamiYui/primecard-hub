package com.primehub.primecardadmin.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    
    String storeFile(MultipartFile file);
    
    void deleteFile(String filename);
    
    String getFileExtension(String filename);
    
    long parseSize(String sizeStr);
}