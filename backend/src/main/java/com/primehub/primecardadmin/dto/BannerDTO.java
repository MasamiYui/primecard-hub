package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.entity.BannerStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class BannerDTO {
    private Long id;
    
    @NotBlank(message = "轮播图标题不能为空")
    @Size(max = 100, message = "轮播图标题长度不能超过100个字符")
    private String title;
    
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;
    
    @NotNull(message = "链接类型不能为空")
    private BannerLinkType linkType;
    
    private String linkUrl;
    
    private Long linkId;
    
    private String linkAppid;
    
    private String linkPage;
    
    private Integer sortOrder;
    
    private BannerStatus status;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer viewCount;
    
    private Integer clickCount;
    
    private Long createdById;
    
    private String createdByUsername;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    // Constructors
    public BannerDTO() {}

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BannerLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(BannerLinkType linkType) {
        this.linkType = linkType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public String getLinkAppid() {
        return linkAppid;
    }

    public void setLinkAppid(String linkAppid) {
        this.linkAppid = linkAppid;
    }

    public String getLinkPage() {
        return linkPage;
    }

    public void setLinkPage(String linkPage) {
        this.linkPage = linkPage;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public BannerStatus getStatus() {
        return status;
    }

    public void setStatus(BannerStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
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