package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.DashboardStatsDTO;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 仪表盘统计数据DTO
     */
    DashboardStatsDTO getDashboardStats();

    /**
     * 获取资讯趋势数据
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 日期-数量映射
     */
    Map<String, Long> getNewsTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 获取热门分类数据
     *
     * @return 分类名称-数量映射
     */
    Map<String, Long> getPopularCategories();

    /**
     * 获取热门标签数据
     *
     * @return 标签名称-数量映射
     */
    Map<String, Long> getPopularTags();

    /**
     * 获取用户增长数据
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 日期-数量映射
     */
    Map<String, Long> getUserGrowth(LocalDate startDate, LocalDate endDate);

    /**
     * 获取信用卡统计数据
     *
     * @return 银行名称-数量映射
     */
    Map<String, Long> getCreditCardsByBank();

    /**
     * 获取信用卡类型统计数据
     *
     * @return 卡片类型-数量映射
     */
    Map<String, Long> getCreditCardsByType();

    /**
     * 获取信用卡级别统计数据
     *
     * @return 卡片级别-数量映射
     */
    Map<String, Long> getCreditCardsByLevel();
}