package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.NewsCreateDTO;
import com.primehub.primecardadmin.dto.NewsDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<NewsDTO>>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<Long> tagIds) {
        
        PageResponseDTO<NewsDTO> newsPage = newsService.getAllNews(
                page, size, keyword, categoryId, status, authorId, startDate, endDate, tagIds);
        
        return ResponseEntity.ok(ApiResponseDTO.success("获取资讯列表成功", newsPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<NewsDTO>> getNewsById(@PathVariable Long id) {
        NewsDTO newsDTO = newsService.getNewsById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("获取资讯详情成功", newsDTO));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<NewsDTO>> createNews(@Valid @RequestBody NewsCreateDTO newsCreateDTO) {
        NewsDTO createdNews = newsService.createNews(newsCreateDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("创建资讯成功", createdNews));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<NewsDTO>> updateNews(
            @PathVariable Long id,
            @Valid @RequestBody NewsCreateDTO newsUpdateDTO) {
        NewsDTO updatedNews = newsService.updateNews(id, newsUpdateDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("更新资讯成功", updatedNews));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok(ApiResponseDTO.success("删除资讯成功"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<NewsDTO>> updateNewsStatus(
            @PathVariable Long id,
            @RequestParam NewsStatus status) {
        NewsDTO updatedNews = newsService.updateNewsStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success("更新资讯状态成功", updatedNews));
    }

    @PutMapping("/{id}/view-count")
    public ResponseEntity<ApiResponseDTO<Void>> incrementViewCount(@PathVariable Long id) {
        newsService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponseDTO.success("增加浏览量成功"));
    }
}