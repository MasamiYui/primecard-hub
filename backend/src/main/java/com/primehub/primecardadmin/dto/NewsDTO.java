package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.NewsStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class NewsDTO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private Long categoryId;
    private String categoryName;
    private NewsStatus status;
    private String coverImage;
    private Set<TagDTO> tags = new HashSet<>();
    private Long authorId;
    private String authorName;
    private Integer viewCount;
    private LocalDateTime publishTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NewsDTO() {}

    public NewsDTO(Long id, String title, String content, String summary, Long categoryId, 
                   String categoryName, NewsStatus status, String coverImage, Set<TagDTO> tags, 
                   Long authorId, String authorName, Integer viewCount, LocalDateTime publishTime, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
        this.coverImage = coverImage;
        this.tags = tags != null ? tags : new HashSet<>();
        this.authorId = authorId;
        this.authorName = authorName;
        this.viewCount = viewCount;
        this.publishTime = publishTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}