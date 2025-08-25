package com.primehub.primecardadmin.service.client.impl;

import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.TagDTO;
import com.primehub.primecardadmin.dto.client.NewsClientDTO;
import com.primehub.primecardadmin.dto.client.NewsClientListDTO;
import com.primehub.primecardadmin.entity.News;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.entity.Tag;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.NewsRepository;
import com.primehub.primecardadmin.service.client.NewsClientService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * C端新闻资讯服务实现类
 */
@Service
public class NewsClientServiceImpl implements NewsClientService {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public PageResponseDTO<NewsClientListDTO> getNewsList(int page, int size, Long categoryId, String keyword, List<Long> tagIds) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime"));
        
        Specification<News> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询已发布的新闻
            predicates.add(criteriaBuilder.equal(root.get("status"), NewsStatus.PUBLISHED));
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("summary"), "%" + keyword + "%")
                ));
            }
            
            // 分类ID
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
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
        
        List<NewsClientListDTO> newsDTOs = newsPage.getContent().stream()
                .map(this::convertToNewsClientListDTO)
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
    public List<NewsClientListDTO> getRecommendedNews(int limit) {
        // 这里可以根据业务需求实现推荐算法，例如根据标签、分类等
        // 简单实现：获取最新发布的新闻
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "publishTime"));
        Page<News> newsPage = newsRepository.findByStatus(NewsStatus.PUBLISHED, pageable);
        
        return newsPage.getContent().stream()
                .map(this::convertToNewsClientListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsClientListDTO> getHotNews(int limit) {
        // 获取浏览量最高的新闻
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "viewCount"));
        Page<News> newsPage = newsRepository.findByStatus(NewsStatus.PUBLISHED, pageable);
        
        return newsPage.getContent().stream()
                .map(this::convertToNewsClientListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NewsClientDTO getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻资讯不存在"));
        
        // 只允许访问已发布的新闻
        if (news.getStatus() != NewsStatus.PUBLISHED) {
            throw new ResourceNotFoundException("新闻资讯不存在或未发布");
        }
        
        return convertToNewsClientDTO(news);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻资讯不存在"));
        
        // 只允许增加已发布新闻的浏览量
        if (news.getStatus() == NewsStatus.PUBLISHED) {
            news.setViewCount(news.getViewCount() + 1);
            newsRepository.save(news);
        }
    }

    @Override
    @Transactional
    public Integer likeNews(Long id) {
        // 实际项目中应该关联用户ID，防止重复点赞
        // 这里简化处理，直接增加点赞数
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻资讯不存在"));
        
        if (news.getStatus() != NewsStatus.PUBLISHED) {
            throw new ResourceNotFoundException("新闻资讯不存在或未发布");
        }
        
        // 假设有一个likeCount字段，这里需要在News实体中添加
        // 临时使用viewCount代替
        news.setViewCount(news.getViewCount() + 1);
        newsRepository.save(news);
        
        return news.getViewCount();
    }

    @Override
    @Transactional
    public Integer unlikeNews(Long id) {
        // 实际项目中应该关联用户ID，防止重复取消点赞
        // 这里简化处理，直接减少点赞数
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("新闻资讯不存在"));
        
        if (news.getStatus() != NewsStatus.PUBLISHED) {
            throw new ResourceNotFoundException("新闻资讯不存在或未发布");
        }
        
        // 假设有一个likeCount字段，这里需要在News实体中添加
        // 临时使用viewCount代替，并确保不会小于0
        int currentCount = news.getViewCount();
        if (currentCount > 0) {
            news.setViewCount(currentCount - 1);
            newsRepository.save(news);
        }
        
        return news.getViewCount();
    }

    /**
     * 将News实体转换为NewsClientListDTO
     */
    private NewsClientListDTO convertToNewsClientListDTO(News news) {
        Set<TagDTO> tagDTOs = news.getTags().stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());
        
        return new NewsClientListDTO(
                news.getId(),
                news.getTitle(),
                news.getSummary(),
                news.getCategory().getId(),
                news.getCategory().getName(),
                news.getCoverImage(),
                tagDTOs,
                news.getAuthor().getUsername(),
                news.getViewCount(),
                news.getViewCount(), // 临时使用viewCount代替likeCount
                news.getPublishTime()
        );
    }

    /**
     * 将News实体转换为NewsClientDTO
     */
    private NewsClientDTO convertToNewsClientDTO(News news) {
        Set<TagDTO> tagDTOs = news.getTags().stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());
        
        return new NewsClientDTO(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getSummary(),
                news.getCategory().getId(),
                news.getCategory().getName(),
                news.getCoverImage(),
                tagDTOs,
                news.getAuthor().getUsername(),
                news.getViewCount(),
                news.getViewCount(), // 临时使用viewCount代替likeCount
                news.getPublishTime(),
                news.getCreatedAt()
        );
    }
}