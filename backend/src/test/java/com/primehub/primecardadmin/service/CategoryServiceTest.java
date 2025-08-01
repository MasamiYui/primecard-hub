package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.CategoryDTO;
import com.primehub.primecardadmin.entity.Category;
import com.primehub.primecardadmin.entity.CategoryStatus;
import com.primehub.primecardadmin.repository.CategoryRepository;
import com.primehub.primecardadmin.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setStatus(CategoryStatus.ACTIVE);
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Test Category");
        categoryDTO.setDescription("Test Description");
        categoryDTO.setStatus(CategoryStatus.ACTIVE);
    }

    @Test
    void getAllCategories_Success() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByStatus(eq(CategoryStatus.ACTIVE), any(Sort.class))).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getAllCategories(CategoryStatus.ACTIVE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
    }

    @Test
    void getCategoryById_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        CategoryDTO result = categoryService.getCategoryById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        assertEquals("Test Description", result.getDescription());
    }

    @Test
    void createCategory_Success() {
        // Given
        when(categoryRepository.existsByName("Test Category")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryDTO result = categoryService.createCategory(categoryDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_NameAlreadyExists_ThrowsException() {
        // Given
        when(categoryRepository.existsByName("Test Category")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(categoryDTO);
        });
    }

    @Test
    void updateCategory_Success() {
        // Given
        CategoryDTO updateDTO = new CategoryDTO();
        updateDTO.setName("Updated Category");
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(CategoryStatus.ACTIVE);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Updated Category")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryDTO result = categoryService.updateCategory(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deleteCategory_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void updateCategoryStatus_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryDTO result = categoryService.updateCategoryStatus(1L, CategoryStatus.INACTIVE);

        // Then
        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void isCategoryNameExists_True() {
        // Given
        when(categoryRepository.existsByName("Test Category")).thenReturn(true);

        // When
        boolean result = categoryService.isCategoryNameExists("Test Category", null);

        // Then
        assertTrue(result);
    }

    @Test
    void isCategoryNameExists_False() {
        // Given
        when(categoryRepository.existsByName("Test Category")).thenReturn(false);

        // When
        boolean result = categoryService.isCategoryNameExists("Test Category", null);

        // Then
        assertFalse(result);
    }
}