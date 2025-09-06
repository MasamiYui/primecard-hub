package com.primehub.primecardadmin.controller.client;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.client.NewsClientDTO;
import com.primehub.primecardadmin.dto.client.NewsClientListDTO;
import com.primehub.primecardadmin.service.client.NewsClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端新闻资讯控制器
 */
@RestController
@RequestMapping("/client/news")
@Tag(name = "C端新闻资讯接口", description = "提供C端新闻资讯相关的API")
public class NewsClientController {

    @Autowired
    private NewsClientService newsClientService;

    @GetMapping
    @Operation(summary = "获取新闻资讯列表", description = "分页获取新闻资讯列表，支持分类、关键词和标签过滤")
    public ApiResponseDTO<PageResponseDTO<NewsClientListDTO>> getNewsList(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "标签ID列表") @RequestParam(required = false) List<Long> tagIds) {
        
        PageResponseDTO<NewsClientListDTO> result = newsClientService.getNewsList(page, size, categoryId, keyword, tagIds);
        return ApiResponseDTO.success(result);
    }

    @GetMapping("/recommended")
    @Operation(summary = "获取推荐新闻资讯", description = "获取推荐的新闻资讯列表")
    public ApiResponseDTO<List<NewsClientListDTO>> getRecommendedNews(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") int limit) {
        
        List<NewsClientListDTO> result = newsClientService.getRecommendedNews(limit);
        return ApiResponseDTO.success(result);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门新闻资讯", description = "获取热门的新闻资讯列表")
    public ApiResponseDTO<List<NewsClientListDTO>> getHotNews(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") int limit) {
        
        List<NewsClientListDTO> result = newsClientService.getHotNews(limit);
        return ApiResponseDTO.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取新闻资讯详情", description = "根据ID获取新闻资讯详情")
    public ApiResponseDTO<NewsClientDTO> getNewsById(
            @Parameter(description = "新闻资讯ID") @PathVariable Long id) {
        
        NewsClientDTO result = newsClientService.getNewsById(id);
        return ApiResponseDTO.success(result);
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "增加新闻资讯浏览量", description = "增加指定新闻资讯的浏览量")
    public ApiResponseDTO<Void> incrementViewCount(
            @Parameter(description = "新闻资讯ID") @PathVariable Long id) {
        
        newsClientService.incrementViewCount(id);
        return ApiResponseDTO.success(null);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞新闻资讯", description = "对指定新闻资讯进行点赞")
    public ApiResponseDTO<Integer> likeNews(
            @Parameter(description = "新闻资讯ID") @PathVariable Long id) {
        
        Integer likeCount = newsClientService.likeNews(id);
        return ApiResponseDTO.success(likeCount);
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞新闻资讯", description = "取消对指定新闻资讯的点赞")
    public ApiResponseDTO<Integer> unlikeNews(
            @Parameter(description = "新闻资讯ID") @PathVariable Long id) {
        
        Integer likeCount = newsClientService.unlikeNews(id);
        return ApiResponseDTO.success(likeCount);
    }
}