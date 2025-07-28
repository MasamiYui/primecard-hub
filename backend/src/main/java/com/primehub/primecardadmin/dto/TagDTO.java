package com.primehub.primecardadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    private Long id;
    
    @NotBlank(message = "标签名不能为空")
    @Size(max = 50, message = "标签名长度不能超过50个字符")
    private String name;
    
    private String color;
    
    private Integer usageCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}