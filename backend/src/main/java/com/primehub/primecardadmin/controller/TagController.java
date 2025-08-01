package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.TagDTO;
import com.primehub.primecardadmin.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<TagDTO>>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        PageResponseDTO<TagDTO> tags = tagService.getAllTags(page, size, keyword);
        return ResponseEntity.ok(ApiResponseDTO.success("获取标签列表成功", tags));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponseDTO<List<TagDTO>>> getPopularTags(
            @RequestParam(defaultValue = "10") int limit) {
        List<TagDTO> popularTags = tagService.getPopularTags(limit);
        return ResponseEntity.ok(ApiResponseDTO.success("获取热门标签成功", popularTags));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TagDTO>> getTagById(@PathVariable Long id) {
        TagDTO tagDTO = tagService.getTagById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("获取标签详情成功", tagDTO));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<TagDTO>> createTag(@Valid @RequestBody TagDTO tagDTO) {
        TagDTO createdTag = tagService.createTag(tagDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("创建标签成功", createdTag));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<TagDTO>> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagDTO tagDTO) {
        TagDTO updatedTag = tagService.updateTag(id, tagDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("更新标签成功", updatedTag));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponseDTO.success("删除标签成功"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<TagDTO>>> searchTags(@RequestParam String keyword) {
        List<TagDTO> tags = tagService.searchTags(keyword);
        return ResponseEntity.ok(ApiResponseDTO.success("搜索标签成功", tags));
    }
}