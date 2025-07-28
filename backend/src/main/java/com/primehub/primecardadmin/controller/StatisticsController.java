package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.DashboardStatsDTO;
import com.primehub.primecardadmin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = statisticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponseDTO.success("获取仪表盘统计数据成功", stats));
    }

    @GetMapping("/news-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getNewsTrend(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Map<String, Long> trend = statisticsService.getNewsTrend(startDate, endDate);
        return ResponseEntity.ok(ApiResponseDTO.success("获取资讯趋势数据成功", trend));
    }

    @GetMapping("/popular-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getPopularCategories() {
        Map<String, Long> categories = statisticsService.getPopularCategories();
        return ResponseEntity.ok(ApiResponseDTO.success("获取热门分类数据成功", categories));
    }

    @GetMapping("/popular-tags")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getPopularTags() {
        Map<String, Long> tags = statisticsService.getPopularTags();
        return ResponseEntity.ok(ApiResponseDTO.success("获取热门标签数据成功", tags));
    }

    @GetMapping("/user-growth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getUserGrowth(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Map<String, Long> growth = statisticsService.getUserGrowth(startDate, endDate);
        return ResponseEntity.ok(ApiResponseDTO.success("获取用户增长数据成功", growth));
    }
}