package com.primehub.primecardadmin.service.client.impl;

import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.client.NewsClientDTO;
import com.primehub.primecardadmin.dto.client.NewsClientListDTO;
import com.primehub.primecardadmin.entity.*;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NewsClientServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsClientServiceImpl newsClientService;

    private News news;
    private Category category;
    private User author;
    private Tag tag1, tag2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // 准备测试数据
        category = new Category();
        category.setId(1L);
        category.setName("测试分类");
        category.setStatus(CategoryStatus.ACTIVE);

        author = new User();
        author.setId(1L);
        author.setUsername("测试作者");
        author.setRole(UserRole.EDITOR);
        author.setStatus(UserStatus.ACTIVE);

        tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("标签1");

        tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("标签2");

        news = new News();
        news.setId(1L);
        news.setTitle("测试新闻标题");
        news.setContent("测试新闻内容");
        news.setSummary("测试新闻摘要");
        news.setCategory(category);
        news.setStatus(NewsStatus.PUBLISHED);
        news.setCoverImage("cover-image.jpg");
        news.setTags(new HashSet<>(Arrays.asList(tag1, tag2)));
        news.setAuthor(author);
        news.setViewCount(100);
        news.setPublishTime(LocalDateTime.now());
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetNewsList() {
        // 准备测试数据
        List<News> newsList = Arrays.asList(news);
        Page<News> newsPage = new PageImpl<>(newsList);

        // 模拟repository行为
        when(newsRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(newsPage);

        // 执行测试
        PageResponseDTO<NewsClientListDTO> result = newsClientService.getNewsList(0, 10, 1L, "测试", Arrays.asList(1L));

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("测试新闻标题", result.getContent().get(0).getTitle());
        assertEquals("测试新闻摘要", result.getContent().get(0).getSummary());
        assertEquals(1L, result.getContent().get(0).getCategoryId());
        assertEquals("测试分类", result.getContent().get(0).getCategoryName());
        assertEquals("测试作者", result.getContent().get(0).getAuthorName());
        assertEquals(100, result.getContent().get(0).getViewCount());
    }

    @Test
    public void testGetRecommendedNews() {
        // 准备测试数据
        List<News> newsList = Arrays.asList(news);
        Page<News> newsPage = new PageImpl<>(newsList);

        // 模拟repository行为
        when(newsRepository.findByStatus(eq(NewsStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(newsPage);

        // 执行测试
        List<NewsClientListDTO> result = newsClientService.getRecommendedNews(5);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试新闻标题", result.get(0).getTitle());
    }

    @Test
    public void testGetHotNews() {
        // 准备测试数据
        List<News> newsList = Arrays.asList(news);
        Page<News> newsPage = new PageImpl<>(newsList);

        // 模拟repository行为
        when(newsRepository.findByStatus(eq(NewsStatus.PUBLISHED), any(Pageable.class)))
                .thenReturn(newsPage);

        // 执行测试
        List<NewsClientListDTO> result = newsClientService.getHotNews(5);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试新闻标题", result.get(0).getTitle());
    }

    @Test
    public void testGetNewsById() {
        // 模拟repository行为
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));

        // 执行测试
        NewsClientDTO result = newsClientService.getNewsById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试新闻标题", result.getTitle());
        assertEquals("测试新闻内容", result.getContent());
        assertEquals("测试新闻摘要", result.getSummary());
        assertEquals(1L, result.getCategoryId());
        assertEquals("测试分类", result.getCategoryName());
        assertEquals("测试作者", result.getAuthorName());
    }

    @Test
    public void testGetNewsByIdNotFound() {
        // 模拟repository行为
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> {
            newsClientService.getNewsById(999L);
        });
    }

    @Test
    public void testGetNewsByIdNotPublished() {
        // 准备测试数据
        News draftNews = new News();
        draftNews.setId(2L);
        draftNews.setTitle("草稿新闻");
        draftNews.setStatus(NewsStatus.DRAFT);
        draftNews.setCategory(category);
        draftNews.setAuthor(author);

        // 模拟repository行为
        when(newsRepository.findById(2L)).thenReturn(Optional.of(draftNews));

        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> {
            newsClientService.getNewsById(2L);
        });
    }

    @Test
    public void testIncrementViewCount() {
        // 模拟repository行为
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));
        when(newsRepository.save(any(News.class))).thenReturn(news);

        // 执行测试
        newsClientService.incrementViewCount(1L);

        // 验证结果
        verify(newsRepository, times(1)).findById(1L);
        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    public void testLikeNews() {
        // 模拟repository行为
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));
        when(newsRepository.save(any(News.class))).thenReturn(news);

        // 执行测试
        Integer result = newsClientService.likeNews(1L);

        // 验证结果
        assertNotNull(result);
        verify(newsRepository, times(1)).findById(1L);
        verify(newsRepository, times(1)).save(any(News.class));
    }

    @Test
    public void testUnlikeNews() {
        // 模拟repository行为
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));
        when(newsRepository.save(any(News.class))).thenReturn(news);

        // 执行测试
        Integer result = newsClientService.unlikeNews(1L);

        // 验证结果
        assertNotNull(result);
        verify(newsRepository, times(1)).findById(1L);
        verify(newsRepository, times(1)).save(any(News.class));
    }
}