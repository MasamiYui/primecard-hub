package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.CategoryDTO;
import com.primehub.primecardadmin.dto.CategoryOrderDTO;
import com.primehub.primecardadmin.entity.Category;
import com.primehub.primecardadmin.entity.CategoryStatus;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.CategoryRepository;
import com.primehub.primecardadmin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllCategories(CategoryStatus status) {
        List<Category> categories;
        
        if (status != null) {
            categories = categoryRepository.findByStatus(status, Sort.by("sortOrder"));
        } else {
            categories = categoryRepository.findAll(Sort.by("sortOrder"));
        }
        
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 检查名称是否已存在
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setIcon(categoryDTO.getIcon());
        category.setSortOrder(getNextSortOrder());
        category.setStatus(categoryDTO.getStatus() != null ? categoryDTO.getStatus() : CategoryStatus.ACTIVE);
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
        
        // 检查名称是否已存在（排除当前分类）
        if (!existingCategory.getName().equals(categoryDTO.getName()) && 
            categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setStatus(categoryDTO.getStatus());
        
        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategoryStatus(Long id, CategoryStatus status) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
        
        category.setStatus(status);
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public List<CategoryDTO> updateCategoriesOrder(List<CategoryOrderDTO> categoryOrders) {
        for (CategoryOrderDTO orderDTO : categoryOrders) {
            Category category = categoryRepository.findById(orderDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("分类不存在: " + orderDTO.getId()));
            
            category.setSortOrder(orderDTO.getOrder());
            categoryRepository.save(category);
        }
        
        List<Category> updatedCategories = categoryRepository.findAll(Sort.by("sortOrder"));
        return updatedCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCategoryNameExists(String name, Long id) {
        if (id == null) {
            return categoryRepository.existsByName(name);
        } else {
            return categoryRepository.findByName(name)
                    .map(category -> !category.getId().equals(id))
                    .orElse(false);
        }
    }
    
    private Integer getNextSortOrder() {
        return categoryRepository.findAll()
                .stream()
                .mapToInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0)
                .max()
                .orElse(0) + 1;
    }
    
    // 辅助方法：将实体转换为DTO
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setSortOrder(category.getSortOrder());
        dto.setStatus(category.getStatus());
        dto.setNewsCount(category.getNews() != null ? category.getNews().size() : 0);
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}