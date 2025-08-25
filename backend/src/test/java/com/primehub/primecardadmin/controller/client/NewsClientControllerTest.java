package com.primehub.primecardadmin.controller.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.TagDTO;
import com.primehub.primecardadmin.dto.client.NewsClientDTO;
import com.primehub.primecardadmin.dto.client.NewsClientListDTO;
import com.primehub.primecardadmin.service.client.NewsClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsClientService newsClientService;

    @Autowired
    private ObjectMapper objectMapper;

    private NewsClientDTO newsClientDTO;
    private NewsClientListDTO newsClientListDTO;
    private PageResponseDTO<NewsClientListDTO> pageResponse;

    @BeforeEach
    public void setup() {
        // 准备测试数据
        TagDTO tag1 = new TagDTO(1L, "标签1");
        TagDTO tag2 = new TagDTO(2L, "标签2");

        newsClientListDTO = new NewsClientListDTO(
                1L,
                "测试新闻标题",
                "测试新闻摘要",
                1L,
                "测试分类",
                "cover-image.jpg",
                new HashSet<>(Arrays.asList(tag1, tag2)),
                "测试作者",
                100,
                50,
                LocalDateTime.now()
        );

        newsClientDTO = new NewsClientDTO(
                1L,
                "测试新闻标题",
                "测试新闻内容",
                "测试新闻摘要",
                1L,
                "测试分类",
                "cover-image.jpg",
                new HashSet<>(Arrays.asList(tag1, tag2)),
                "测试作者",
                100,
                50,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<NewsClientListDTO> content = Arrays.asList(newsClientListDTO);
        pageResponse = new PageResponseDTO<>(
                content,
                0,
                10,
                1,
                1
        );
    }

    @Test
    public void testGetNewsList() throws Exception {
        when(newsClientService.getNewsList(anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/client/news")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("测试新闻标题"));
    }

    @Test
    public void testGetNewsById() throws Exception {
        when(newsClientService.getNewsById(anyLong()))
                .thenReturn(newsClientDTO);

        mockMvc.perform(get("/api/client/news/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("测试新闻标题"))
                .andExpect(jsonPath("$.data.content").value("测试新闻内容"));
    }

    @Test
    public void testGetRecommendedNews() throws Exception {
        when(newsClientService.getRecommendedNews(anyInt()))
                .thenReturn(Arrays.asList(newsClientListDTO));

        mockMvc.perform(get("/api/client/news/recommended")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("测试新闻标题"));
    }

    @Test
    public void testGetHotNews() throws Exception {
        when(newsClientService.getHotNews(anyInt()))
                .thenReturn(Arrays.asList(newsClientListDTO));

        mockMvc.perform(get("/api/client/news/hot")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("测试新闻标题"));
    }

    @Test
    public void testIncrementViewCount() throws Exception {
        mockMvc.perform(post("/api/client/news/1/view")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testLikeNews() throws Exception {
        when(newsClientService.likeNews(anyLong()))
                .thenReturn(51);

        mockMvc.perform(post("/api/client/news/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(51));
    }

    @Test
    public void testUnlikeNews() throws Exception {
        when(newsClientService.unlikeNews(anyLong()))
                .thenReturn(49);

        mockMvc.perform(delete("/api/client/news/1/like")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(49));
    }
}