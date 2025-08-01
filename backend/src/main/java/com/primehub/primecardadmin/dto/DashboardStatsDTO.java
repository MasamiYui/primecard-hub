package com.primehub.primecardadmin.dto;

import java.util.Map;

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

    // 构造函数
    public DashboardStatsDTO() {}

    // Getter和Setter方法
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getNewUsersToday() {
        return newUsersToday;
    }

    public void setNewUsersToday(long newUsersToday) {
        this.newUsersToday = newUsersToday;
    }

    public long getNewUsersThisWeek() {
        return newUsersThisWeek;
    }

    public void setNewUsersThisWeek(long newUsersThisWeek) {
        this.newUsersThisWeek = newUsersThisWeek;
    }

    public long getNewUsersThisMonth() {
        return newUsersThisMonth;
    }

    public void setNewUsersThisMonth(long newUsersThisMonth) {
        this.newUsersThisMonth = newUsersThisMonth;
    }

    public long getTotalNews() {
        return totalNews;
    }

    public void setTotalNews(long totalNews) {
        this.totalNews = totalNews;
    }

    public long getPublishedNews() {
        return publishedNews;
    }

    public void setPublishedNews(long publishedNews) {
        this.publishedNews = publishedNews;
    }

    public long getDraftNews() {
        return draftNews;
    }

    public void setDraftNews(long draftNews) {
        this.draftNews = draftNews;
    }

    public long getNewsViewsToday() {
        return newsViewsToday;
    }

    public void setNewsViewsToday(long newsViewsToday) {
        this.newsViewsToday = newsViewsToday;
    }

    public long getNewsViewsThisWeek() {
        return newsViewsThisWeek;
    }

    public void setNewsViewsThisWeek(long newsViewsThisWeek) {
        this.newsViewsThisWeek = newsViewsThisWeek;
    }

    public long getNewsViewsThisMonth() {
        return newsViewsThisMonth;
    }

    public void setNewsViewsThisMonth(long newsViewsThisMonth) {
        this.newsViewsThisMonth = newsViewsThisMonth;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public long getActiveCategories() {
        return activeCategories;
    }

    public void setActiveCategories(long activeCategories) {
        this.activeCategories = activeCategories;
    }

    public long getTotalTags() {
        return totalTags;
    }

    public void setTotalTags(long totalTags) {
        this.totalTags = totalTags;
    }

    public Map<String, Long> getTopTags() {
        return topTags;
    }

    public void setTopTags(Map<String, Long> topTags) {
        this.topTags = topTags;
    }

    public long getTotalCreditCards() {
        return totalCreditCards;
    }

    public void setTotalCreditCards(long totalCreditCards) {
        this.totalCreditCards = totalCreditCards;
    }

    public long getActiveCreditCards() {
        return activeCreditCards;
    }

    public void setActiveCreditCards(long activeCreditCards) {
        this.activeCreditCards = activeCreditCards;
    }

    public Map<String, Long> getCardsByBank() {
        return cardsByBank;
    }

    public void setCardsByBank(Map<String, Long> cardsByBank) {
        this.cardsByBank = cardsByBank;
    }

    public Map<String, Long> getCardsByType() {
        return cardsByType;
    }

    public void setCardsByType(Map<String, Long> cardsByType) {
        this.cardsByType = cardsByType;
    }

    public Map<String, Long> getCardsByLevel() {
        return cardsByLevel;
    }

    public void setCardsByLevel(Map<String, Long> cardsByLevel) {
        this.cardsByLevel = cardsByLevel;
    }
}