package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.DashboardStatsDTO;
import com.primehub.primecardadmin.entity.CardLevel;
import com.primehub.primecardadmin.entity.CardStatus;
import com.primehub.primecardadmin.entity.CategoryStatus;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.entity.UserStatus;
import com.primehub.primecardadmin.repository.CategoryRepository;
import com.primehub.primecardadmin.repository.CreditCardRepository;
import com.primehub.primecardadmin.repository.NewsRepository;
import com.primehub.primecardadmin.repository.TagRepository;
import com.primehub.primecardadmin.repository.UserRepository;
import com.primehub.primecardadmin.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;
    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public StatisticsServiceImpl(UserRepository userRepository, 
                                CreditCardRepository creditCardRepository,
                                NewsRepository newsRepository,
                                CategoryRepository categoryRepository,
                                TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.creditCardRepository = creditCardRepository;
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // 获取基本统计数据
        stats.setTotalUsers(userRepository.count());
        stats.setTotalCreditCards(creditCardRepository.count());
        stats.setTotalNews(newsRepository.count());
        stats.setTotalCategories(categoryRepository.count());
        stats.setTotalTags(tagRepository.count());
        
        // 设置默认值
        stats.setActiveUsers(0);
        stats.setNewUsersToday(0);
        stats.setNewUsersThisWeek(0);
        stats.setNewUsersThisMonth(0);
        stats.setPublishedNews(0);
        stats.setDraftNews(0);
        stats.setNewsViewsToday(0);
        stats.setNewsViewsThisWeek(0);
        stats.setNewsViewsThisMonth(0);
        stats.setActiveCategories(0);
        stats.setActiveCreditCards(0);
        
        return stats;
    }

    @Override
    public Map<String, Long> getNewsTrend(LocalDate startDate, LocalDate endDate) {
        // 简化实现，返回空的趋势数据
        Map<String, Long> trend = new LinkedHashMap<>();
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(29);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 初始化所有日期的数量为0
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            trend.put(date.format(formatter), 0L);
        }
        
        return trend;
    }

    @Override
    public Map<String, Long> getPopularCategories() {
        // 简化实现，返回空的分类数据
        return new LinkedHashMap<>();
    }

    @Override
    public Map<String, Long> getPopularTags() {
        // 简化实现，返回空的标签数据
        return new LinkedHashMap<>();
    }

    @Override
    public Map<String, Long> getUserGrowth(LocalDate startDate, LocalDate endDate) {
        // 简化实现，返回空的用户增长数据
        Map<String, Long> growth = new LinkedHashMap<>();
        
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(29);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 初始化所有日期的数量为0
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            growth.put(date.format(formatter), 0L);
        }
        
        return growth;
    }

    @Override
    public Map<String, Long> getCreditCardsByBank() {
        // 简化实现，返回空的银行数据
        return new LinkedHashMap<>();
    }

    @Override
    public Map<String, Long> getCreditCardsByType() {
        // 简化实现，返回空的卡片类型数据
        return new LinkedHashMap<>();
    }

    @Override
    public Map<String, Long> getCreditCardsByLevel() {
        // 简化实现，返回空的卡片等级数据
        return new LinkedHashMap<>();
    }
}