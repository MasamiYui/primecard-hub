package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.CategoryDTO;
import com.primehub.primecardadmin.dto.CategoryOrderDTO;
import com.primehub.primecardadmin.entity.CategoryStatus;
import com.primehub.primecardadmin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CategoryDTO>>> getAllCategories(
            @RequestParam(required = false) CategoryStatus status) {
        List<CategoryDTO> categories = categoryService.getAllCategories(status);
        return ResponseEntity.ok(ApiResponseDTO.success("获取分类列表成功", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("获取分类详情成功", categoryDTO));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("创建分类成功", createdCategory));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("更新分类成功", updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponseDTO.success("删除分类成功"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<CategoryDTO>> updateCategoryStatus(
            @PathVariable Long id,
            @RequestParam CategoryStatus status) {
        CategoryDTO updatedCategory = categoryService.updateCategoryStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success("更新分类状态成功", updatedCategory));
    }

    @PutMapping("/order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<CategoryDTO>>> updateCategoriesOrder(
            @RequestBody List<CategoryOrderDTO> categoryOrders) {
        List<CategoryDTO> updatedCategories = categoryService.updateCategoriesOrder(categoryOrders);
        return ResponseEntity.ok(ApiResponseDTO.success("更新分类排序成功", updatedCategories));
    }
}