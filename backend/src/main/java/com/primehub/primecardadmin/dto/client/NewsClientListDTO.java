package com.primehub.primecardadmin.dto.client;

import com.primehub.primecardadmin.dto.TagDTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * C端新闻资讯列表项DTO
 */
public class NewsClientListDTO {
    private Long id;
    private String title;
    private String summary;
    private Long categoryId;
    private String categoryName;
    private String coverImage;
    private Set<TagDTO> tags = new HashSet<>();
    private String authorName;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime publishTime;

    public NewsClientListDTO() {}

    public NewsClientListDTO(Long id, String title, String summary, Long categoryId,
                           String categoryName, String coverImage, Set<TagDTO> tags,
                           String authorName, Integer viewCount, Integer likeCount, 
                           LocalDateTime publishTime) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.coverImage = coverImage;
        this.tags = tags != null ? tags : new HashSet<>();
        this.authorName = authorName;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.publishTime = publishTime;
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

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}