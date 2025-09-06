package com.primehub.primecardadmin.util;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.entity.News;
import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.repository.BannerRepository;
import com.primehub.primecardadmin.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/debug/relation")
public class BannerNewsRelationController {

    @Autowired
    private BannerRepository bannerRepository;
    
    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/banner-news")
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
    
    @PostMapping("/fix-banner-news/{bannerId}")
    public ApiResponseDTO<String> fixBannerNewsRelation(@PathVariable Long bannerId, @RequestParam Long newsId) {
        Optional<Banner> bannerOpt = bannerRepository.findById(bannerId);
        Optional<News> newsOpt = newsRepository.findById(newsId);
        
        if (!bannerOpt.isPresent()) {
            return ApiResponseDTO.error("Banner不存在");
        }
        
        if (!newsOpt.isPresent()) {
            return ApiResponseDTO.error("新闻不存在");
        }
        
        Banner banner = bannerOpt.get();
        banner.setLinkType(BannerLinkType.NEWS);
        banner.setLinkId(newsId);
        banner.setLinkUrl(null); // 清空URL，使用ID关联
        
        bannerRepository.save(banner);
        
        return ApiResponseDTO.success("成功修复Banner " + bannerId + " 与新闻 " + newsId + " 的关联关系", 
                                      "Banner: " + banner.getTitle() + " -> News: " + newsOpt.get().getTitle());
    }
    
    @GetMapping("/recommend-relations")
    public ApiResponseDTO<Map<String, Object>> recommendBannerNewsRelations() {
        List<Banner> allBanners = bannerRepository.findAll();
        List<News> allNews = newsRepository.findAll();
        
        Map<String, Object> recommendations = new HashMap<>();
        List<Map<String, Object>> suggestionList = new ArrayList<>();
        
        // 查找没有关联新闻的banner
        for (Banner banner : allBanners) {
            if (banner.getLinkType() != BannerLinkType.NEWS || banner.getLinkId() == null) {
                Map<String, Object> suggestion = new HashMap<>();
                suggestion.put("bannerId", banner.getId());
                suggestion.put("bannerTitle", banner.getTitle());
                suggestion.put("currentLinkType", banner.getLinkType());
                
                // 基于标题相似度推荐新闻
                List<Map<String, Object>> recommendedNews = new ArrayList<>();
                for (News news : allNews.subList(0, Math.min(3, allNews.size()))) {
                    Map<String, Object> newsInfo = new HashMap<>();
                    newsInfo.put("id", news.getId());
                    newsInfo.put("title", news.getTitle());
                    newsInfo.put("similarity", calculateSimilarity(banner.getTitle(), news.getTitle()));
                    recommendedNews.add(newsInfo);
                }
                
                suggestion.put("recommendedNews", recommendedNews);
                suggestionList.add(suggestion);
            }
        }
        
        recommendations.put("suggestions", suggestionList);
        recommendations.put("totalSuggestions", suggestionList.size());
        
        return ApiResponseDTO.success("Banner与新闻关联建议", recommendations);
    }
    
    private double calculateSimilarity(String title1, String title2) {
        // 简单的相似度计算，基于共同字符
        String[] words1 = title1.split("");
        String[] words2 = title2.split("");
        
        int commonCount = 0;
        for (String word : words1) {
            if (title2.contains(word)) {
                commonCount++;
            }
        }
        
        return (double) commonCount / Math.max(words1.length, words2.length);
    }
}