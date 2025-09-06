package com.primehub.primecardadmin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BannerLinkType linkType;

    private String linkUrl;

    private Long linkId;

    private String linkAppid;

    private String linkPage;

    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BannerStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer viewCount;

    private Integer clickCount;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Banner() {}

    public Banner(Long id, String title, String imageUrl, BannerLinkType linkType, String linkUrl,
                 Long linkId, String linkAppid, String linkPage, Integer sortOrder, BannerStatus status,
                 LocalDateTime startTime, LocalDateTime endTime, Integer viewCount, Integer clickCount,
                 User createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkType = linkType;
        this.linkUrl = linkUrl;
        this.linkId = linkId;
        this.linkAppid = linkAppid;
        this.linkPage = linkPage;
        this.sortOrder = sortOrder;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.viewCount = viewCount;
        this.clickCount = clickCount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (status == null) {
            status = BannerStatus.ACTIVE;
        }
        if (viewCount == null) {
            viewCount = 0;
        }
        if (clickCount == null) {
            clickCount = 0;
        }
        if (linkType == null) {
            linkType = BannerLinkType.NONE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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