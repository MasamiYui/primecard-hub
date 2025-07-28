package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.NewsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCreateDTO {
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @NotBlank(message = "摘要不能为空")
    @Size(max = 200, message = "摘要长度不能超过200个字符")
    private String summary;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    private NewsStatus status;
    
    private String coverImage;
    
    private Set<Long> tagIds = new HashSet<>();
    
    private LocalDateTime publishTime;
}