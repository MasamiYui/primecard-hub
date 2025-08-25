package com.primehub.primecardadmin.service.client;

import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.client.NewsClientDTO;
import com.primehub.primecardadmin.dto.client.NewsClientListDTO;

import java.util.List;

/**
 * C端新闻资讯服务接口
 */
public interface NewsClientService {

    /**
     * 获取新闻资讯列表（分页）
     *
     * @param page       页码
     * @param size       每页大小
     * @param categoryId 分类ID（可选）
     * @param keyword    关键词（可选）
     * @param tagIds     标签ID列表（可选）
     * @return 分页结果
     */
    PageResponseDTO<NewsClientListDTO> getNewsList(int page, int size, Long categoryId, String keyword, List<Long> tagIds);

    /**
     * 获取推荐新闻资讯列表
     *
     * @param limit 数量限制
     * @return 推荐新闻列表
     */
    List<NewsClientListDTO> getRecommendedNews(int limit);

    /**
     * 获取热门新闻资讯列表
     *
     * @param limit 数量限制
     * @return 热门新闻列表
     */
    List<NewsClientListDTO> getHotNews(int limit);

    /**
     * 根据ID获取新闻资讯详情
     *
     * @param id 新闻资讯ID
     * @return 新闻资讯详情
     */
    NewsClientDTO getNewsById(Long id);

    /**
     * 增加新闻资讯浏览量
     *
     * @param id 新闻资讯ID
     */
    void incrementViewCount(Long id);

    /**
     * 点赞新闻资讯
     *
     * @param id 新闻资讯ID
     * @return 点赞后的点赞数
     */
    Integer likeNews(Long id);

    /**
     * 取消点赞新闻资讯
     *
     * @param id 新闻资讯ID
     * @return 取消点赞后的点赞数
     */
    Integer unlikeNews(Long id);
}