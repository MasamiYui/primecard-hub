package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.DashboardStatsDTO;
import com.primehub.primecardadmin.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDashboardStats() throws Exception {
        // 准备测试数据
        DashboardStatsDTO dashboardStats = new DashboardStatsDTO();
        dashboardStats.setTotalUsers(100);
        dashboardStats.setTotalCreditCards(50);
        dashboardStats.setTotalNews(30);
        dashboardStats.setTotalCategories(5);
        dashboardStats.setTotalTags(10);
        dashboardStats.setActiveUsers(80);
        dashboardStats.setPublishedNews(25);
        dashboardStats.setActiveCreditCards(45);

        when(statisticsService.getDashboardStats()).thenReturn(dashboardStats);

        // 执行测试
        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取仪表盘统计数据成功"))
                .andExpect(jsonPath("$.data.totalUsers").value(100))
                .andExpect(jsonPath("$.data.totalCreditCards").value(50))
                .andExpect(jsonPath("$.data.totalNews").value(30))
                .andExpect(jsonPath("$.data.totalCategories").value(5))
                .andExpect(jsonPath("$.data.totalTags").value(10))
                .andExpect(jsonPath("$.data.activeUsers").value(80))
                .andExpect(jsonPath("$.data.publishedNews").value(25))
                .andExpect(jsonPath("$.data.activeCreditCards").value(45));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCreditCardsByBank() throws Exception {
        // 准备测试数据
        Map<String, Long> cardsByBank = new LinkedHashMap<>();
        cardsByBank.put("招商银行", 15L);
        cardsByBank.put("工商银行", 10L);
        cardsByBank.put("建设银行", 8L);

        when(statisticsService.getCreditCardsByBank()).thenReturn(cardsByBank);

        // 执行测试
        mockMvc.perform(get("/api/statistics/credit-cards-by-bank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取按银行统计的信用卡数据成功"))
                .andExpect(jsonPath("$.data.招商银行").value(15))
                .andExpect(jsonPath("$.data.工商银行").value(10))
                .andExpect(jsonPath("$.data.建设银行").value(8));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCreditCardsByType() throws Exception {
        // 准备测试数据
        Map<String, Long> cardsByType = new LinkedHashMap<>();
        cardsByType.put("CREDIT", 25L);
        cardsByType.put("DEBIT", 15L);
        cardsByType.put("PREPAID", 10L);

        when(statisticsService.getCreditCardsByType()).thenReturn(cardsByType);

        // 执行测试
        mockMvc.perform(get("/api/statistics/credit-cards-by-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取按类型统计的信用卡数据成功"))
                .andExpect(jsonPath("$.data.CREDIT").value(25))
                .andExpect(jsonPath("$.data.DEBIT").value(15))
                .andExpect(jsonPath("$.data.PREPAID").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCreditCardsByLevel() throws Exception {
        // 准备测试数据
        Map<String, Long> cardsByLevel = new LinkedHashMap<>();
        cardsByLevel.put("STANDARD", 20L);
        cardsByLevel.put("GOLD", 15L);
        cardsByLevel.put("PLATINUM", 10L);
        cardsByLevel.put("DIAMOND", 5L);

        when(statisticsService.getCreditCardsByLevel()).thenReturn(cardsByLevel);

        // 执行测试
        mockMvc.perform(get("/api/statistics/credit-cards-by-level"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取按级别统计的信用卡数据成功"))
                .andExpect(jsonPath("$.data.STANDARD").value(20))
                .andExpect(jsonPath("$.data.GOLD").value(15))
                .andExpect(jsonPath("$.data.PLATINUM").value(10))
                .andExpect(jsonPath("$.data.DIAMOND").value(5));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void testUnauthorizedAccess() throws Exception {
        // 测试权限不足的情况
        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedAccess() throws Exception {
        // 测试未认证的情况
        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}