package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.NewsStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    
    public NewsCreateDTO() {}
    
    public NewsCreateDTO(String title, String content, String summary, Long categoryId, 
                        NewsStatus status, String coverImage, Set<Long> tagIds, LocalDateTime publishTime) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.categoryId = categoryId;
        this.status = status;
        this.coverImage = coverImage;
        this.tagIds = tagIds != null ? tagIds : new HashSet<>();
        this.publishTime = publishTime;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public NewsStatus getStatus() {
        return status;
    }
    
    public void setStatus(NewsStatus status) {
        this.status = status;
    }
    
    public String getCoverImage() {
        return coverImage;
    }
    
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
    
    public Set<Long> getTagIds() {
        return tagIds;
    }
    
    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds != null ? tagIds : new HashSet<>();
    }
    
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}