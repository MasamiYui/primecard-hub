package com.primehub.primecardadmin.util;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.entity.News;
import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.repository.BannerRepository;
import com.primehub.primecardadmin.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/debug")
public class DatabaseDebugController {

    @Autowired
    private BannerRepository bannerRepository;
    
    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/banners/all")
    public ApiResponseDTO<List<Banner>> getAllBanners() {
        List<Banner> banners = bannerRepository.findAll();
        return ApiResponseDTO.success("查询所有banner数据", banners);
    }

    @GetMapping("/banners/active")
    public ApiResponseDTO<List<Banner>> getActiveBanners() {
        List<Banner> activeBanners = bannerRepository.findActiveBanners(LocalDateTime.now());
        return ApiResponseDTO.success("查询活跃banner数据", activeBanners);
    }

    @GetMapping("/banners/debug")
    public ApiResponseDTO<Map<String, Object>> debugBanners() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        // 查询所有banner
        List<Banner> allBanners = bannerRepository.findAll();
        debugInfo.put("allBannersCount", allBanners.size());
        debugInfo.put("allBanners", allBanners);
        
        // 查询活跃banner
        List<Banner> activeBanners = bannerRepository.findActiveBanners(LocalDateTime.now());
        debugInfo.put("activeBannersCount", activeBanners.size());
        debugInfo.put("activeBanners", activeBanners);
        
        // 当前时间
        LocalDateTime now = LocalDateTime.now();
        debugInfo.put("currentTime", now);
        
        return ApiResponseDTO.success("Banner调试信息", debugInfo);
    }
    
    @GetMapping("/banners/news-relation")
    public ApiResponseDTO<Map<String, Object>> getBannerNewsRelation() {
        Map<String, Object> relationInfo = new HashMap<>();
        
        // 获取所有banner
        List<Banner> allBanners = bannerRepository.findAll();
        
        // 获取所有新闻
        List<News> allNews = newsRepository.findAll();
        
        // 分析关联关系
        List<Map<String, Object>> newsLinkedBanners = new ArrayList<>();
        List<Map<String, Object>> cardLinkedBanners = new ArrayList<>();
        List<Map<String, Object>> unrelatedBanners = new ArrayList<>();
        
        for (Banner banner : allBanners) {
            Map<String, Object> bannerInfo = new HashMap<>();
            bannerInfo.put("id", banner.getId());
            bannerInfo.put("title", banner.getTitle());
            bannerInfo.put("linkType", banner.getLinkType());
            bannerInfo.put("linkId", banner.getLinkId());
            bannerInfo.put("sortOrder", banner.getSortOrder());
            
            if (banner.getLinkType() == BannerLinkType.NEWS && banner.getLinkId() != null) {
                Optional<News> linkedNews = newsRepository.findById(banner.getLinkId());
                if (linkedNews.isPresent()) {
                    bannerInfo.put("linkedNewsTitle", linkedNews.get().getTitle());
                    bannerInfo.put("linkedNewsStatus", linkedNews.get().getStatus());
                    bannerInfo.put("relationStatus", "VALID");
                } else {
                    bannerInfo.put("linkedNewsTitle", "新闻不存在");
                    bannerInfo.put("relationStatus", "BROKEN");
                }
                newsLinkedBanners.add(bannerInfo);
            } else if (banner.getLinkType() == BannerLinkType.CARD) {
                bannerInfo.put("relationStatus", "CARD_LINK");
                cardLinkedBanners.add(bannerInfo);
            } else {
                bannerInfo.put("relationStatus", "NO_RELATION");
                unrelatedBanners.add(bannerInfo);
            }
        }
        
        // 统计新闻数据
        List<Map<String, Object>> newsInfo = new ArrayList<>();
        for (News news : allNews.subList(0, Math.min(10, allNews.size()))) { // 只显示前10条
            Map<String, Object> newsData = new HashMap<>();
            newsData.put("id", news.getId());
            newsData.put("title", news.getTitle());
            newsData.put("status", news.getStatus());
            newsData.put("categoryName", news.getCategory() != null ? news.getCategory().getName() : "无分类");
            newsData.put("viewCount", news.getViewCount());
            
            // 检查是否有banner关联到此新闻
            boolean hasLinkedBanner = allBanners.stream()
                .anyMatch(b -> b.getLinkType() == BannerLinkType.NEWS && 
                              news.getId().equals(b.getLinkId()));
            newsData.put("hasLinkedBanner", hasLinkedBanner);
            
            newsInfo.add(newsData);
        }
        
        relationInfo.put("newsLinkedBanners", newsLinkedBanners);
        relationInfo.put("cardLinkedBanners", cardLinkedBanners);
        relationInfo.put("unrelatedBanners", unrelatedBanners);
        relationInfo.put("newsData", newsInfo);
        relationInfo.put("totalBanners", allBanners.size());
        relationInfo.put("totalNews", allNews.size());
        relationInfo.put("newsLinkedCount", newsLinkedBanners.size());
        relationInfo.put("cardLinkedCount", cardLinkedBanners.size());
        
        return ApiResponseDTO.success("Banner与新闻关联关系分析", relationInfo);
    }
}