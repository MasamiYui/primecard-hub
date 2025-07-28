package com.primehub.primecardadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // 用户统计
    private long totalUsers;
    private long activeUsers;
    private long newUsersToday;
    private long newUsersThisWeek;
    private long newUsersThisMonth;

    // 资讯统计
    private long totalNews;
    private long publishedNews;
    private long draftNews;
    private long newsViewsToday;
    private long newsViewsThisWeek;
    private long newsViewsThisMonth;

    // 分类统计
    private long totalCategories;
    private long activeCategories;

    // 标签统计
    private long totalTags;
    private Map<String, Long> topTags;

    // 信用卡统计
    private long totalCreditCards;
    private long activeCreditCards;
    private Map<String, Long> cardsByBank;
    private Map<String, Long> cardsByType;
    private Map<String, Long> cardsByLevel;
}