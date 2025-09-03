package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.NewsCreateDTO;
import com.primehub.primecardadmin.dto.NewsDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.TagDTO;
import com.primehub.primecardadmin.entity.Category;
import com.primehub.primecardadmin.entity.News;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.entity.Tag;
import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.CategoryRepository;
import com.primehub.primecardadmin.repository.NewsRepository;
import com.primehub.primecardadmin.repository.TagRepository;
import com.primehub.primecardadmin.repository.UserRepository;
import com.primehub.primecardadmin.service.NewsService;
import com.primehub.primecardadmin.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagService tagService;

    @Override
    public PageResponseDTO<NewsDTO> getAllNews(int page, int size, String keyword, Long categoryId, 
                                              NewsStatus status, Long authorId, LocalDateTime startDate, 
                                              LocalDateTime endDate, List<Long> tagIds) {
        logger.info("获取新闻列表 - 页码: {}, 大小: {}, 关键词: {}, 分类ID: {}, 状态: {}", 
                   page, size, keyword, categoryId, status);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<News> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("summary"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("content"), "%" + keyword + "%")
                ));
            }
            
            // 分类ID
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            
            // 资讯状态
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // 作者ID
            if (authorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("author").get("id"), authorId));
            }
            
            // 日期范围
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            // 标签ID列表
            if (tagIds != null && !tagIds.isEmpty()) {
                Join<News, Tag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(tagJoin.get("id").in(tagIds));
                query.distinct(true); // 避免重复结果
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<News> newsPage = newsRepository.findAll(spec, pageable);
        List<NewsDTO> newsDTOs = newsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                newsDTOs,
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalElements(),
                newsPage.getTotalPages()
        );
    }

    @Override
    public NewsDTO getNewsById(Long id) {
        logger.info("获取新闻详情 - ID: {}", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("新闻不存在 - ID: {}", id);
                    return new ResourceNotFoundException("资讯不存在");
                });
        logger.debug("成功获取新闻详情 - ID: {}, 标题: {}", id, news.getTitle());
        return convertToDTO(news);
    }

    @Override
    @Transactional
    public NewsDTO createNews(NewsCreateDTO newsCreateDTO) {
        logger.info("创建新闻 - 标题: {}, 分类ID: {}, 状态: {}", 
                   newsCreateDTO.getTitle(), newsCreateDTO.getCategoryId(), newsCreateDTO.getStatus());
        
        // 获取分类
        Category category = categoryRepository.findById(newsCreateDTO.getCategoryId())
                .orElseThrow(() -> {
                    logger.error("分类不存在 - ID: {}", newsCreateDTO.getCategoryId());
                    return new ResourceNotFoundException("分类不存在");
                });
        
        // 获取作者 - 这里应该从当前登录用户获取，暂时使用ID为1的用户
        User author = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        // 创建资讯
        News news = new News();
        news.setTitle(newsCreateDTO.getTitle());
        news.setSummary(newsCreateDTO.getSummary());
        news.setContent(newsCreateDTO.getContent());
        news.setCoverImage(newsCreateDTO.getCoverImage());
        news.setCategory(category);
        news.setAuthor(author);
        news.setStatus(newsCreateDTO.getStatus() != null ? newsCreateDTO.getStatus() : NewsStatus.DRAFT);
        news.setViewCount(0);
        
        // 处理标签
        Set<Tag> tags = new HashSet<>();
        if (newsCreateDTO.getTagIds() != null && !newsCreateDTO.getTagIds().isEmpty()) {
            for (Long tagId : newsCreateDTO.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("标签不存在: " + tagId));
                tags.add(tag);
                
                // 增加标签使用计数
                tagService.incrementTagUsageCount(tagId);
            }
        }
        news.setTags(tags);
        
        News savedNews = newsRepository.save(news);
        logger.info("新闻创建成功 - ID: {}, 标题: {}", savedNews.getId(), savedNews.getTitle());
        return convertToDTO(savedNews);
    }

    @Override
    @Transactional
    public NewsDTO updateNews(Long id, NewsCreateDTO newsUpdateDTO) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("资讯不存在"));
        
        // 获取分类
        Category category = categoryRepository.findById(newsUpdateDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("分类不存在"));
        
        // 更新资讯信息
        existingNews.setTitle(newsUpdateDTO.getTitle());
        existingNews.setSummary(newsUpdateDTO.getSummary());
        existingNews.setContent(newsUpdateDTO.getContent());
        existingNews.setCoverImage(newsUpdateDTO.getCoverImage());
        existingNews.setCategory(category);
        existingNews.setStatus(newsUpdateDTO.getStatus());
        existingNews.setUpdatedAt(LocalDateTime.now());
        
        // 处理标签
        Set<Long> oldTagIds = existingNews.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        
        Set<Long> newTagIds = new HashSet<>(newsUpdateDTO.getTagIds());
        
        // 移除旧标签
        for (Long tagId : oldTagIds) {
            if (!newTagIds.contains(tagId)) {
                tagService.decrementTagUsageCount(tagId);
            }
        }
        
        // 添加新标签
        for (Long tagId : newTagIds) {
            if (!oldTagIds.contains(tagId)) {
                tagService.incrementTagUsageCount(tagId);
            }
        }
        
        // 更新标签集合
        Set<Tag> tags = new HashSet<>();
        for (Long tagId : newTagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new ResourceNotFoundException("标签不存在: " + tagId));
            tags.add(tag);
        }
        existingNews.setTags(tags);
        
        News updatedNews = newsRepository.save(existingNews);
        return convertToDTO(updatedNews);
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        logger.info("删除新闻 - ID: {}", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("尝试删除不存在的新闻 - ID: {}", id);
                    return new ResourceNotFoundException("资讯不存在");
                });
        
        logger.debug("删除新闻: {} (ID: {}), 关联标签数: {}", news.getTitle(), id, news.getTags().size());
        
        // 减少标签使用计数
        for (Tag tag : news.getTags()) {
            tagService.decrementTagUsageCount(tag.getId());
        }
        
        newsRepository.delete(news);
        logger.info("新闻删除成功 - ID: {}", id);
    }

    @Override
    @Transactional
    public NewsDTO updateNewsStatus(Long id, NewsStatus status) {
        logger.info("更新新闻状态 - ID: {}, 新状态: {}", id, status);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("尝试更新不存在的新闻状态 - ID: {}", id);
                    return new ResourceNotFoundException("资讯不存在");
                });
        
        NewsStatus oldStatus = news.getStatus();
        news.setStatus(status);
        news.setUpdatedAt(LocalDateTime.now());
        
        News updatedNews = newsRepository.save(news);
        logger.info("新闻状态更新成功 - ID: {}, 从 {} 更新为 {}", id, oldStatus, status);
        return convertToDTO(updatedNews);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        logger.debug("增加新闻浏览量 - ID: {}", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("尝试增加不存在新闻的浏览量 - ID: {}", id);
                    return new ResourceNotFoundException("资讯不存在");
                });
        
        news.setViewCount(news.getViewCount() + 1);
        newsRepository.save(news);
    }

    @Override
    public List<NewsDTO> getHotNews(int limit) {
        List<News> hotNews = newsRepository.findByStatusOrderByViewCountDesc(
                NewsStatus.PUBLISHED, PageRequest.of(0, limit));
        
        return hotNews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsDTO> getLatestNews(int limit) {
        List<News> latestNews = newsRepository.findByStatusOrderByCreatedAtDesc(
                NewsStatus.PUBLISHED, PageRequest.of(0, limit));
        
        return latestNews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponseDTO<NewsDTO> getNewsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<News> newsPage = newsRepository.findByCategoryIdAndStatus(
                categoryId, NewsStatus.PUBLISHED, pageable);
        
        List<NewsDTO> newsDTOs = newsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                newsDTOs,
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalElements(),
                newsPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<NewsDTO> getNewsByTag(Long tagId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<News> newsPage = newsRepository.findByTagsIdAndStatus(
                tagId, NewsStatus.PUBLISHED, pageable);
        
        List<NewsDTO> newsDTOs = newsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponseDTO<>(
                newsDTOs,
                newsPage.getNumber(),
                newsPage.getSize(),
                newsPage.getTotalElements(),
                newsPage.getTotalPages()
        );
    }
    
    // 辅助方法：将实体转换为DTO
    private NewsDTO convertToDTO(News news) {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setId(news.getId());
        newsDTO.setTitle(news.getTitle());
        newsDTO.setSummary(news.getSummary());
        newsDTO.setContent(news.getContent());
        newsDTO.setCoverImage(news.getCoverImage());
        newsDTO.setCategoryId(news.getCategory().getId());
        newsDTO.setCategoryName(news.getCategory().getName());
        newsDTO.setAuthorId(news.getAuthor().getId());
        newsDTO.setAuthorName(news.getAuthor().getUsername());
        newsDTO.setStatus(news.getStatus());
        newsDTO.setViewCount(news.getViewCount());
        newsDTO.setPublishTime(news.getPublishTime());
        newsDTO.setCreatedAt(news.getCreatedAt());
        newsDTO.setUpdatedAt(news.getUpdatedAt());
        
        // 转换标签
        Set<TagDTO> tagDTOs = new HashSet<>();
        for (Tag tag : news.getTags()) {
            TagDTO tagDTO = new TagDTO();
            tagDTO.setId(tag.getId());
            tagDTO.setName(tag.getName());
            tagDTO.setColor(tag.getColor());
            tagDTO.setUsageCount(tag.getUsageCount());
            tagDTO.setCreatedAt(tag.getCreatedAt());
            tagDTO.setUpdatedAt(tag.getUpdatedAt());
            tagDTOs.add(tagDTO);
        }
        newsDTO.setTags(tagDTOs);
        
        return newsDTO;
    }
}