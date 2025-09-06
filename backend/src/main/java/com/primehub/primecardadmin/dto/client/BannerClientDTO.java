package com.primehub.primecardadmin.dto.client;

import com.primehub.primecardadmin.entity.BannerLinkType;

public class BannerClientDTO {
    private Long id;
    private String title;
    private String imageUrl;
    private BannerLinkType linkType;
    private String linkUrl;
    private Long linkId;
    private String linkAppid;
    private String linkPage;

    // Constructors
    public BannerClientDTO() {}

    public BannerClientDTO(Long id, String title, String imageUrl, BannerLinkType linkType, 
                          String linkUrl, Long linkId, String linkAppid, String linkPage) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkType = linkType;
        this.linkUrl = linkUrl;
        this.linkId = linkId;
        this.linkAppid = linkAppid;
        this.linkPage = linkPage;
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
}