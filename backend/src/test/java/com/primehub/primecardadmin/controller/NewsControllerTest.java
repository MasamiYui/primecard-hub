package com.primehub.primecardadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primehub.primecardadmin.dto.NewsDTO;
import com.primehub.primecardadmin.dto.NewsCreateDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllNews() throws Exception {
        // 准备测试数据
        NewsDTO news1 = createTestNewsDTO(1L, "测试新闻1", "测试内容1", NewsStatus.PUBLISHED);
        NewsDTO news2 = createTestNewsDTO(2L, "测试新闻2", "测试内容2", NewsStatus.DRAFT);
        
        List<NewsDTO> newsList = Arrays.asList(news1, news2);
        PageResponseDTO<NewsDTO> pageResponse = new PageResponseDTO<>();
        pageResponse.setContent(newsList);
        pageResponse.setTotalElements(2L);
        pageResponse.setTotalPages(1);
        pageResponse.setPageNumber(0);
        pageResponse.setPageSize(10);
        
        when(newsService.getAllNews(anyInt(), anyInt(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        // 执行测试
        mockMvc.perform(get("/api/news")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取资讯列表成功"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetNewsById() throws Exception {
        // 准备测试数据
        Long newsId = 1L;
        NewsDTO news = createTestNewsDTO(newsId, "测试新闻", "测试内容", NewsStatus.PUBLISHED);
        
        when(newsService.getNewsById(newsId)).thenReturn(news);

        // 执行测试
        mockMvc.perform(get("/api/news/{id}", newsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取资讯详情成功"))
                .andExpect(jsonPath("$.data.id").value(newsId))
                .andExpect(jsonPath("$.data.title").value("测试新闻"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateNews() throws Exception {
        // Create test NewsCreateDTO
        NewsCreateDTO newsCreateDTO = new NewsCreateDTO();
        newsCreateDTO.setTitle("Test News");
        newsCreateDTO.setContent("Test Content");
        newsCreateDTO.setSummary("Test Summary");
        newsCreateDTO.setCategoryId(1L);
        newsCreateDTO.setStatus(NewsStatus.DRAFT);

        NewsDTO createdNews = new NewsDTO();
        createdNews.setId(1L);
        createdNews.setTitle("Test News");
        createdNews.setContent("Test Content");
        createdNews.setSummary("Test Summary");
        createdNews.setCategoryId(1L);
        createdNews.setStatus(NewsStatus.DRAFT);

        when(newsService.createNews(any(NewsCreateDTO.class))).thenReturn(createdNews);

        mockMvc.perform(post("/api/news")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test News"));

        verify(newsService).createNews(any(NewsCreateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateNews() throws Exception {
        // 准备测试数据
        Long newsId = 1L;
        NewsCreateDTO updateNewsDTO = new NewsCreateDTO();
        updateNewsDTO.setTitle("更新新闻");
        updateNewsDTO.setContent("更新内容");
        updateNewsDTO.setSummary("更新摘要");
        updateNewsDTO.setStatus(NewsStatus.PUBLISHED);

        NewsDTO updatedNews = createTestNewsDTO(newsId, "更新新闻", "更新内容", NewsStatus.PUBLISHED);
        
        when(newsService.updateNews(eq(newsId), any(NewsCreateDTO.class))).thenReturn(updatedNews);

        // 执行测试
        mockMvc.perform(put("/api/news/{id}", newsId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNewsDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新资讯成功"))
                .andExpect(jsonPath("$.data.title").value("更新新闻"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateNewsStatus() throws Exception {
        // 准备测试数据
        Long newsId = 1L;
        NewsStatus newStatus = NewsStatus.PUBLISHED;
        
        NewsDTO updatedNews = createTestNewsDTO(newsId, "测试新闻", "测试内容", newStatus);
        
        when(newsService.updateNewsStatus(newsId, newStatus)).thenReturn(updatedNews);

        // 执行测试
        mockMvc.perform(patch("/api/news/{id}/status", newsId)
                        .with(csrf())
                        .param("status", newStatus.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新资讯状态成功"))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void testUnauthorizedNewsCreation() throws Exception {
        // 测试权限不足的情况
        NewsDTO createNewsDTO = new NewsDTO();
        createNewsDTO.setTitle("新建新闻");

        mockMvc.perform(post("/api/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createNewsDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedAccess() throws Exception {
        // 测试未认证的情况
        mockMvc.perform(get("/api/news"))
                .andExpect(status().isUnauthorized());
    }

    private NewsDTO createTestNewsDTO(Long id, String title, String content, NewsStatus status) {
        NewsDTO news = new NewsDTO();
        news.setId(id);
        news.setTitle(title);
        news.setContent(content);
        news.setSummary(title + "摘要");
        news.setStatus(status);
        news.setViewCount(100);
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());
        return news;
    }
}