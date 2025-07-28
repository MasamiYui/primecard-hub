package com.primehub.primecardadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {

    private String fileName;
    private String fileUrl;
    private String fileType;
    private long size;
}