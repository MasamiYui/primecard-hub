package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.CategoryStatus;
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
public class CategoryDTO {
    private Long id;
    
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;
    
    private String description;
    
    private String icon;
    
    private Integer sortOrder;
    
    private CategoryStatus status;
    
    private Integer newsCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}