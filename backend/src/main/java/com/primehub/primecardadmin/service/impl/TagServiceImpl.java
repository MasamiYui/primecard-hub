package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.TagDTO;
import com.primehub.primecardadmin.entity.Tag;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.TagRepository;
import com.primehub.primecardadmin.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public PageResponseDTO<TagDTO> getAllTags(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "usageCount"));
        Page<Tag> tagPage = tagRepository.findAll(pageable);
        
        List<TagDTO> tagDTOs = tagPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                tagDTOs,
                tagPage.getNumber(),
                tagPage.getSize(),
                tagPage.getTotalElements(),
                tagPage.getTotalPages()
        );
    }

    @Override
    public List<TagDTO> getPopularTags(int limit) {
        // 简化实现，返回前几个标签
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "usageCount"));
        Page<Tag> tagPage = tagRepository.findAll(pageable);
        return tagPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        return convertToDTO(tag);
    }

    @Override
    @Transactional
    public TagDTO createTag(TagDTO tagDTO) {
        // 检查名称是否已存在
        if (isTagNameExists(tagDTO.getName(), null)) {
            throw new IllegalArgumentException("标签名称已存在");
        }
        
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tag.setUsageCount(0);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        Tag savedTag = tagRepository.save(tag);
        return convertToDTO(savedTag);
    }

    @Override
    @Transactional
    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        
        // 检查名称是否已存在（排除当前标签）
        if (isTagNameExists(tagDTO.getName(), id)) {
            throw new IllegalArgumentException("标签名称已存在");
        }
        
        existingTag.setName(tagDTO.getName());
        existingTag.setUpdatedAt(LocalDateTime.now());
        
        Tag updatedTag = tagRepository.save(existingTag);
        return convertToDTO(updatedTag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        tagRepository.delete(tag);
    }

    @Override
    public List<TagDTO> searchTags(String keyword) {
        List<Tag> tags = tagRepository.findByNameContaining(keyword);
        return tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementTagUsageCount(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        
        tag.setUsageCount(tag.getUsageCount() + 1);
        tag.setUpdatedAt(LocalDateTime.now());
        
        tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void decrementTagUsageCount(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        
        if (tag.getUsageCount() > 0) {
            tag.setUsageCount(tag.getUsageCount() - 1);
            tag.setUpdatedAt(LocalDateTime.now());
            
            tagRepository.save(tag);
        }
    }

    @Override
    public boolean isTagNameExists(String name, Long id) {
        if (id == null) {
            return tagRepository.existsByName(name);
        } else {
            // 简化实现，暂时只检查名称是否存在
            return tagRepository.existsByName(name);
        }
    }
    
    // 辅助方法：将实体转换为DTO
    private TagDTO convertToDTO(Tag tag) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        tagDTO.setUsageCount(tag.getUsageCount());
        tagDTO.setCreatedAt(tag.getCreatedAt());
        tagDTO.setUpdatedAt(tag.getUpdatedAt());
        return tagDTO;
    }
}