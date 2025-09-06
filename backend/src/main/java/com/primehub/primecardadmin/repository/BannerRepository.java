package com.primehub.primecardadmin.repository;

import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.entity.BannerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long>, JpaSpecificationExecutor<Banner> {
    
    /**
     * 根据状态查询轮播图
     */
    Page<Banner> findByStatus(BannerStatus status, Pageable pageable);
    
    /**
     * 根据排序查询轮播图
     */
    List<Banner> findAllByOrderBySortOrderAsc();
    
    /**
     * 查询当前有效的轮播图（状态为ACTIVE且在展示时间范围内）
     */
    @Query("SELECT b FROM Banner b WHERE b.status = 'ACTIVE' AND "
           + "(b.startTime IS NULL OR b.startTime <= ?1) AND "
           + "(b.endTime IS NULL OR b.endTime >= ?1) "
           + "ORDER BY b.sortOrder ASC")
    List<Banner> findActiveBanners(LocalDateTime now);
    
    /**
     * 查询即将过期的轮播图
     */
    List<Banner> findByStatusAndEndTimeBetween(BannerStatus status, LocalDateTime start, LocalDateTime end);
    
    /**
     * 查询点击量最高的轮播图
     */
    List<Banner> findByStatusOrderByClickCountDesc(BannerStatus status, Pageable pageable);
}