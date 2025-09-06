package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.config.OssConfig;
import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.FileUploadResponseDTO;
import com.primehub.primecardadmin.service.FileStorageService;
import com.primehub.primecardadmin.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件上传和管理接口")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private OssService ossService;
    
    @Autowired
    private OssConfig.OssProperties ossProperties;

    @Operation(summary = "上传文件到本地", description = "上传文件到本地服务器")
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDTO<FileUploadResponseDTO>> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        FileUploadResponseDTO response = new FileUploadResponseDTO(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok(ApiResponseDTO.success("文件上传成功", response));
    }
    
    @Operation(summary = "上传文件到OSS", description = "上传文件到阿里云OSS")
    @PostMapping("/oss/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDTO<FileUploadResponseDTO>> uploadToOss(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件类型（banner/news/avatar）") @RequestParam(value = "type", defaultValue = "avatar") String type) {
        
        // 根据类型选择路径前缀
        String pathPrefix;
        switch (type.toLowerCase()) {
            case "banner":
                pathPrefix = ossProperties.getPathPrefix().getBanner();
                break;
            case "news":
                pathPrefix = ossProperties.getPathPrefix().getNews();
                break;
            case "avatar":
                pathPrefix = ossProperties.getPathPrefix().getAvatar();
                break;
            default:
                pathPrefix = ossProperties.getPathPrefix().getAvatar();
        }
        
        // 上传到OSS
        String fileUrl = ossService.uploadFile(file, pathPrefix);
        
        FileUploadResponseDTO response = new FileUploadResponseDTO(
                ossService.generateUniqueFileName(file.getOriginalFilename()),
                fileUrl,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok(ApiResponseDTO.success("OSS文件上传成功", response));
    }

    @Operation(summary = "删除本地文件", description = "删除本地服务器上的文件")
    @DeleteMapping("/{fileName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDTO<Void>> deleteFile(
            @Parameter(description = "文件名") @PathVariable String fileName) {
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.ok(ApiResponseDTO.success("文件删除成功"));
    }
    
    @Operation(summary = "删除OSS文件", description = "删除阿里云OSS上的文件")
    @DeleteMapping("/oss")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDTO<Void>> deleteOssFile(
            @Parameter(description = "文件URL") @RequestParam("fileUrl") String fileUrl) {
        ossService.deleteFile(fileUrl);
        return ResponseEntity.ok(ApiResponseDTO.success("OSS文件删除成功"));
    }
}