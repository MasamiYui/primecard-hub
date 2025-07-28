package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.CategoryDTO;
import com.primehub.primecardadmin.dto.CategoryOrderDTO;
import com.primehub.primecardadmin.entity.CategoryStatus;

import java.util.List;

public interface CategoryService {

    /**
     * 获取所有分类
     *
     * @param status 分类状态（可选）
     * @return 分类列表
     */
    List<CategoryDTO> getAllCategories(CategoryStatus status);

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类DTO
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * 创建分类
     *
     * @param categoryDTO 分类DTO
     * @return 创建的分类DTO
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * 更新分类
     *
     * @param id          分类ID
     * @param categoryDTO 分类DTO
     * @return 更新后的分类DTO
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 新状态
     * @return 更新后的分类DTO
     */
    CategoryDTO updateCategoryStatus(Long id, CategoryStatus status);

    /**
     * 更新分类排序
     *
     * @param categoryOrders 分类排序DTO列表
     * @return 更新后的分类DTO列表
     */
    List<CategoryDTO> updateCategoriesOrder(List<CategoryOrderDTO> categoryOrders);

    /**
     * 检查分类名称是否存在
     *
     * @param name 分类名称
     * @param id   排除的分类ID（可选，用于更新时检查）
     * @return 是否存在
     */
    boolean isCategoryNameExists(String name, Long id);
}