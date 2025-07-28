package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.TagDTO;

import java.util.List;

public interface TagService {

    /**
     * 获取所有标签（分页）
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词搜索（可选）
     * @return 标签分页结果
     */
    PageResponseDTO<TagDTO> getAllTags(int page, int size, String keyword);

    /**
     * 获取热门标签
     *
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<TagDTO> getPopularTags(int limit);

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签DTO
     */
    TagDTO getTagById(Long id);

    /**
     * 创建标签
     *
     * @param tagDTO 标签DTO
     * @return 创建的标签DTO
     */
    TagDTO createTag(TagDTO tagDTO);

    /**
     * 更新标签
     *
     * @param id     标签ID
     * @param tagDTO 标签DTO
     * @return 更新后的标签DTO
     */
    TagDTO updateTag(Long id, TagDTO tagDTO);

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    void deleteTag(Long id);

    /**
     * 搜索标签
     *
     * @param keyword 关键词
     * @return 标签列表
     */
    List<TagDTO> searchTags(String keyword);

    /**
     * 增加标签使用计数
     *
     * @param id 标签ID
     */
    void incrementTagUsageCount(Long id);

    /**
     * 减少标签使用计数
     *
     * @param id 标签ID
     */
    void decrementTagUsageCount(Long id);

    /**
     * 检查标签名称是否存在
     *
     * @param name 标签名称
     * @param id   排除的标签ID（可选，用于更新时检查）
     * @return 是否存在
     */
    boolean isTagNameExists(String name, Long id);
}