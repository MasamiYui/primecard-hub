package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.BannerCreateDTO;
import com.primehub.primecardadmin.dto.BannerDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.client.BannerClientDTO;
import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.entity.BannerStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerService {

    /**
     * 获取所有轮播图（分页）
     */
    PageResponseDTO<BannerDTO> getAllBanners(int page, int size, String keyword, BannerStatus status, 
                                           BannerLinkType linkType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 根据ID获取轮播图
     */
    BannerDTO getBannerById(Long id);

    /**
     * 创建轮播图
     */
    BannerDTO createBanner(BannerCreateDTO bannerCreateDTO);

    /**
     * 更新轮播图
     */
    BannerDTO updateBanner(Long id, BannerCreateDTO bannerUpdateDTO);

    /**
     * 删除轮播图
     */
    void deleteBanner(Long id);

    /**
     * 更新轮播图状态
     */
    BannerDTO updateBannerStatus(Long id, BannerStatus status);

    /**
     * 调整轮播图顺序
     */
    void updateBannerOrder(List<Long> bannerIds);

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