package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.NewsCreateDTO;
import com.primehub.primecardadmin.dto.NewsDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.Category;
import com.primehub.primecardadmin.entity.News;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.CategoryRepository;
import com.primehub.primecardadmin.repository.NewsRepository;
import com.primehub.primecardadmin.repository.TagRepository;
import com.primehub.primecardadmin.repository.UserRepository;
import com.primehub.primecardadmin.service.TagService;
import com.primehub.primecardadmin.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagService tagService;

    @InjectMocks
    private NewsServiceImpl newsService;

    private News testNews;
    private NewsCreateDTO newsCreateDTO;

    @BeforeEach
    void setUp() {
        // 创建测试用的Category
        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        // 创建测试用的User
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testNews = new News();
        testNews.setId(1L);
        testNews.setTitle("Test News");
        testNews.setContent("Test Content");
        testNews.setStatus(NewsStatus.PUBLISHED);
        testNews.setCategory(testCategory);
        testNews.setAuthor(testUser);
        testNews.setTags(new HashSet<>());
        testNews.setCreatedAt(LocalDateTime.now());
        testNews.setUpdatedAt(LocalDateTime.now());

        newsCreateDTO = new NewsCreateDTO();
        newsCreateDTO.setTitle("Test News");
        newsCreateDTO.setContent("Test Content");
        newsCreateDTO.setSummary("Test Summary");
        newsCreateDTO.setCategoryId(1L);
        newsCreateDTO.setStatus(NewsStatus.PUBLISHED);
    }

    @Test
    void getAllNews_Success() {
        // Given
        List<News> newsList = Arrays.asList(testNews);
        Page<News> newsPage = new PageImpl<>(newsList);
        when(newsRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(newsPage);

        // When
        PageResponseDTO<NewsDTO> result = newsService.getAllNews(0, 10, null, null, NewsStatus.PUBLISHED, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test News", result.getContent().get(0).getTitle());
    }

    @Test
    void getNewsById_Success() {
        // Given
        when(newsRepository.findById(1L)).thenReturn(Optional.of(testNews));

        // When
        NewsDTO result = newsService.getNewsById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test News", result.getTitle());
        assertEquals("Test Content", result.getContent());
    }

    @Test
    void getNewsById_NotFound_ThrowsException() {
        // Given
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            newsService.getNewsById(999L);
        });
    }

    @Test
    void createNews_Success() {
        // Given
        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        
        User testAuthor = new User();
        testAuthor.setId(1L);
        testAuthor.setUsername("testuser");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(newsRepository.save(any(News.class))).thenReturn(testNews);

        // When
        NewsDTO result = newsService.createNews(newsCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test News", result.getTitle());
        assertEquals("Test Content", result.getContent());
        verify(newsRepository).save(any(News.class));
    }

    @Test
    void updateNews_Success() {
        // Given
        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        
        NewsCreateDTO updateDTO = new NewsCreateDTO();
        updateDTO.setTitle("Updated News");
        updateDTO.setContent("Updated Content");
        updateDTO.setSummary("Updated Summary");
        updateDTO.setCategoryId(1L);
        updateDTO.setStatus(NewsStatus.DRAFT);
        updateDTO.setTagIds(new HashSet<>()); // 空标签列表

        News updatedNews = new News();
        updatedNews.setId(1L);
        updatedNews.setTitle("Updated News");
        updatedNews.setContent("Updated Content");
        updatedNews.setStatus(NewsStatus.DRAFT);
        updatedNews.setCategory(testCategory); // 设置Category
        updatedNews.setAuthor(testNews.getAuthor()); // 设置Author

        when(newsRepository.findById(1L)).thenReturn(Optional.of(testNews));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(newsRepository.save(any(News.class))).thenReturn(updatedNews);

        // When
        NewsDTO result = newsService.updateNews(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated News", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        assertEquals(NewsStatus.DRAFT, result.getStatus());
    }

    @Test
    void updateNews_NotFound_ThrowsException() {
        // Given
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        NewsCreateDTO updateDTO = new NewsCreateDTO();
        updateDTO.setTitle("Updated Title");

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            newsService.updateNews(999L, updateDTO);
        });
    }

    @Test
    void deleteNews_Success() {
        // Given
        when(newsRepository.findById(1L)).thenReturn(Optional.of(testNews));

        // When
        newsService.deleteNews(1L);

        // Then
        verify(newsRepository).delete(testNews);
    }

    @Test
    void deleteNews_NotFound_ThrowsException() {
        // Given
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            newsService.deleteNews(999L);
        });
    }

    @Test
    void updateNewsStatus_Success() {
        // Given
        News updatedNews = new News();
        updatedNews.setId(1L);
        updatedNews.setTitle("Test News");
        updatedNews.setContent("Test Content");
        updatedNews.setStatus(NewsStatus.DRAFT);
        updatedNews.setCategory(testNews.getCategory()); // 设置Category
        updatedNews.setAuthor(testNews.getAuthor()); // 设置Author
        updatedNews.setTags(new HashSet<>()); // 设置Tags

        when(newsRepository.findById(1L)).thenReturn(Optional.of(testNews));
        when(newsRepository.save(any(News.class))).thenReturn(updatedNews);

        // When
        NewsDTO result = newsService.updateNewsStatus(1L, NewsStatus.DRAFT);

        // Then
        assertNotNull(result);
        assertEquals(NewsStatus.DRAFT, result.getStatus());
        verify(newsRepository).save(any(News.class));
    }

    @Test
    void updateNewsStatus_NotFound_ThrowsException() {
        // Given
        when(newsRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            newsService.updateNewsStatus(999L, NewsStatus.DRAFT);
        });
    }
}