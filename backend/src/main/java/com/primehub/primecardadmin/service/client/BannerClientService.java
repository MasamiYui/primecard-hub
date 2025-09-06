package com.primehub.primecardadmin.service.client;

import com.primehub.primecardadmin.dto.client.BannerClientDTO;

import java.util.List;

public interface BannerClientService {

    /**
     * 获取活跃的轮播图（用于C端展示）
     */
    List<BannerClientDTO> getActiveBanners();

    /**
     * 记录轮播图点击
     */
    void recordBannerClick(Long id);

    /**
     * 记录轮播图浏览
     */
    void recordBannerView(Long id);
}