package com.primehub.primecardadmin.repository;

import com.primehub.primecardadmin.entity.Category;
import com.primehub.primecardadmin.entity.News;
import com.primehub.primecardadmin.entity.NewsStatus;
import com.primehub.primecardadmin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    
    Page<News> findByStatus(NewsStatus status, Pageable pageable);
    
    Page<News> findByAuthor(User author, Pageable pageable);
    
    Page<News> findByCategory(Category category, Pageable pageable);
    
    Page<News> findByCategoryAndStatus(Category category, NewsStatus status, Pageable pageable);
    
    @Query("SELECT n FROM News n WHERE n.title LIKE %?1% OR n.content LIKE %?1% OR n.summary LIKE %?1%")
    Page<News> searchByKeyword(String keyword, Pageable pageable);
    
    List<News> findByStatusAndPublishTimeBefore(NewsStatus status, LocalDateTime dateTime);
    
    @Query("SELECT n FROM News n JOIN n.tags t WHERE t.id = ?1")
    Page<News> findByTagId(Long tagId, Pageable pageable);
    
    List<News> findByStatusOrderByViewCountDesc(NewsStatus status, Pageable pageable);
    
    List<News> findByStatusOrderByCreatedAtDesc(NewsStatus status, Pageable pageable);
    
    Page<News> findByCategoryIdAndStatus(Long categoryId, NewsStatus status, Pageable pageable);
    
    Page<News> findByTagsIdAndStatus(Long tagId, NewsStatus status, Pageable pageable);
}