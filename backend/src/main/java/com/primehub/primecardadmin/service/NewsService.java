package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.NewsCreateDTO;
import com.primehub.primecardadmin.dto.NewsDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.NewsStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsService {

    /**
     * 获取所有资讯（分页）
     *
     * @param page      页码
     * @param size      每页大小
     * @param keyword   关键词搜索
     * @param categoryId 分类ID
     * @param status    资讯状态
     * @param authorId  作者ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param tagIds    标签ID列表
     * @return 资讯分页结果
     */
    PageResponseDTO<NewsDTO> getAllNews(int page, int size, String keyword, Long categoryId, 
                                       NewsStatus status, Long authorId, LocalDateTime startDate, 
                                       LocalDateTime endDate, List<Long> tagIds);

    /**
     * 根据ID获取资讯
     *
     * @param id 资讯ID
     * @return 资讯DTO
     */
    NewsDTO getNewsById(Long id);

    /**
     * 创建资讯
     *
     * @param newsCreateDTO 资讯创建DTO
     * @return 创建的资讯DTO
     */
    NewsDTO createNews(NewsCreateDTO newsCreateDTO);

    /**
     * 更新资讯
     *
     * @param id            资讯ID
     * @param newsUpdateDTO 资讯更新DTO
     * @return 更新后的资讯DTO
     */
    NewsDTO updateNews(Long id, NewsCreateDTO newsUpdateDTO);

    /**
     * 删除资讯
     *
     * @param id 资讯ID
     */
    void deleteNews(Long id);

    /**
     * 更新资讯状态
     *
     * @param id     资讯ID
     * @param status 新状态
     * @return 更新后的资讯DTO
     */
    NewsDTO updateNewsStatus(Long id, NewsStatus status);

    /**
     * 增加资讯浏览量
     *
     * @param id 资讯ID
     */
    void incrementViewCount(Long id);

    /**
     * 获取热门资讯
     *
     * @param limit 限制数量
     * @return 热门资讯列表
     */
    List<NewsDTO> getHotNews(int limit);

    /**
     * 获取最新资讯
     *
     * @param limit 限制数量
     * @return 最新资讯列表
     */
    List<NewsDTO> getLatestNews(int limit);

    /**
     * 根据分类获取资讯
     *
     * @param categoryId 分类ID
     * @param page       页码
     * @param size       每页大小
     * @return 资讯分页结果
     */
    PageResponseDTO<NewsDTO> getNewsByCategory(Long categoryId, int page, int size);

    /**
     * 根据标签获取资讯
     *
     * @param tagId 标签ID
     * @param page  页码
     * @param size  每页大小
     * @return 资讯分页结果
     */
    PageResponseDTO<NewsDTO> getNewsByTag(Long tagId, int page, int size);
}