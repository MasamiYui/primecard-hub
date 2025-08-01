package com.primehub.primecardadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.primehub.primecardadmin.dto.CategoryDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.CategoryStatus;
import com.primehub.primecardadmin.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO createTestCategoryDTO(Long id, String name, CategoryStatus status) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(id);
        categoryDTO.setName(name);
        categoryDTO.setDescription("测试分类描述");
        categoryDTO.setStatus(status);
        categoryDTO.setSortOrder(1);
        categoryDTO.setCreatedAt(LocalDateTime.now());
        categoryDTO.setUpdatedAt(LocalDateTime.now());
        return categoryDTO;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllCategories() throws Exception {
        // 准备测试数据
        CategoryDTO category1 = createTestCategoryDTO(1L, "信用卡资讯", CategoryStatus.ACTIVE);
        CategoryDTO category2 = createTestCategoryDTO(2L, "优惠活动", CategoryStatus.ACTIVE);

        when(categoryService.getAllCategories(any())).thenReturn(Arrays.asList(category1, category2));

        // 执行测试
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("信用卡资讯"))
                .andExpect(jsonPath("$.data[1].name").value("优惠活动"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCategoryById() throws Exception {
        Long categoryId = 1L;
        CategoryDTO categoryDTO = createTestCategoryDTO(categoryId, "信用卡资讯", CategoryStatus.ACTIVE);

        when(categoryService.getCategoryById(categoryId)).thenReturn(categoryDTO);

        mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(categoryId))
                .andExpect(jsonPath("$.data.name").value("信用卡资讯"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory() throws Exception {
        // 准备测试数据
        CategoryDTO createCategoryDTO = new CategoryDTO();
        createCategoryDTO.setName("新建分类");
        createCategoryDTO.setDescription("新建分类描述");
        createCategoryDTO.setStatus(CategoryStatus.ACTIVE);
        createCategoryDTO.setSortOrder(1);

        CategoryDTO savedCategory = createTestCategoryDTO(3L, "新建分类", CategoryStatus.ACTIVE);
        
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(savedCategory);

        // 执行测试
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCategoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("创建分类成功"))
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.name").value("新建分类"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory() throws Exception {
        Long categoryId = 1L;
        
        // 准备更新数据
        CategoryDTO updateCategoryDTO = new CategoryDTO();
        updateCategoryDTO.setName("更新分类");
        updateCategoryDTO.setDescription("更新分类描述");
        updateCategoryDTO.setStatus(CategoryStatus.ACTIVE);
        updateCategoryDTO.setSortOrder(2);

        CategoryDTO updatedCategory = createTestCategoryDTO(categoryId, "更新分类", CategoryStatus.ACTIVE);
        
        when(categoryService.updateCategory(eq(categoryId), any(CategoryDTO.class))).thenReturn(updatedCategory);

        // 执行测试
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCategoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新分类成功"))
                .andExpect(jsonPath("$.data.id").value(categoryId))
                .andExpect(jsonPath("$.data.name").value("更新分类"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategoryStatus() throws Exception {
        Long categoryId = 1L;
        CategoryStatus newStatus = CategoryStatus.INACTIVE;
        
        CategoryDTO updatedCategory = createTestCategoryDTO(categoryId, "测试分类", newStatus);
        
        when(categoryService.updateCategoryStatus(categoryId, newStatus)).thenReturn(updatedCategory);

        mockMvc.perform(patch("/api/categories/{id}/status", categoryId)
                        .with(csrf())
                        .param("status", newStatus.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("更新分类状态成功"))
                .andExpect(jsonPath("$.data.status").value(newStatus.toString()));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void testUnauthorizedCategoryCreation() throws Exception {
        CategoryDTO createCategoryDTO = new CategoryDTO();
        createCategoryDTO.setName("未授权分类");

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCategoryDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }
}