package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.BannerCreateDTO;
import com.primehub.primecardadmin.dto.BannerDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.client.BannerClientDTO;
import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.entity.BannerStatus;
import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.BannerRepository;
import com.primehub.primecardadmin.repository.UserRepository;
import com.primehub.primecardadmin.service.BannerService;
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

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BannerServiceImpl implements BannerService {

    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PageResponseDTO<BannerDTO> getAllBanners(int page, int size, String keyword, BannerStatus status,
                                                  BannerLinkType linkType, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("获取轮播图列表 - 页码: {}, 大小: {}, 关键词: {}, 状态: {}, 链接类型: {}",
                page, size, keyword, status, linkType);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder"));

        Specification<Banner> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("imageUrl"), "%" + keyword + "%")
                ));
            }

            // 状态
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // 链接类型
            if (linkType != null) {
                predicates.add(criteriaBuilder.equal(root.get("linkType"), linkType));
            }

            // 开始日期
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            // 结束日期
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Banner> bannerPage = bannerRepository.findAll(spec, pageable);
        List<BannerDTO> bannerDTOs = bannerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<BannerDTO>(
                bannerDTOs,
                bannerPage.getNumber(),
                bannerPage.getSize(),
                bannerPage.getTotalElements(),
                bannerPage.getTotalPages()
        );
    }

    @Override
    public BannerDTO getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("轮播图不存在"));
        return convertToDTO(banner);
    }

    @Override
    @Transactional
    public BannerDTO createBanner(BannerCreateDTO bannerCreateDTO) {
        logger.info("创建轮播图 - 标题: {}, 链接类型: {}", bannerCreateDTO.getTitle(), bannerCreateDTO.getLinkType());

        // 获取当前用户 - 这里应该从当前登录用户获取，暂时使用ID为1的用户
        User createdBy = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        Banner banner = new Banner();
        banner.setTitle(bannerCreateDTO.getTitle());
        banner.setImageUrl(bannerCreateDTO.getImageUrl());
        banner.setLinkType(bannerCreateDTO.getLinkType());
        banner.setLinkUrl(bannerCreateDTO.getLinkUrl());
        banner.setLinkId(bannerCreateDTO.getLinkId());
        banner.setLinkAppid(bannerCreateDTO.getLinkAppid());
        banner.setLinkPage(bannerCreateDTO.getLinkPage());
        banner.setStatus(bannerCreateDTO.getStatus() != null ? bannerCreateDTO.getStatus() : BannerStatus.ACTIVE);
        banner.setStartTime(bannerCreateDTO.getStartTime());
        banner.setEndTime(bannerCreateDTO.getEndTime());
        banner.setCreatedBy(createdBy);

        // 如果没有指定排序，则放到最后
        if (bannerCreateDTO.getSortOrder() == null) {
            banner.setSortOrder(getNextSortOrder());
        } else {
            banner.setSortOrder(bannerCreateDTO.getSortOrder());
        }

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Override
    @Transactional
    public BannerDTO updateBanner(Long id, BannerCreateDTO bannerUpdateDTO) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("轮播图不存在"));

        existingBanner.setTitle(bannerUpdateDTO.getTitle());
        existingBanner.setImageUrl(bannerUpdateDTO.getImageUrl());
        existingBanner.setLinkType(bannerUpdateDTO.getLinkType());
        existingBanner.setLinkUrl(bannerUpdateDTO.getLinkUrl());
        existingBanner.setLinkId(bannerUpdateDTO.getLinkId());
        existingBanner.setLinkAppid(bannerUpdateDTO.getLinkAppid());
        existingBanner.setLinkPage(bannerUpdateDTO.getLinkPage());
        
        if (bannerUpdateDTO.getStatus() != null) {
            existingBanner.setStatus(bannerUpdateDTO.getStatus());
        }
        
        existingBanner.setStartTime(bannerUpdateDTO.getStartTime());
        existingBanner.setEndTime(bannerUpdateDTO.getEndTime());

        if (bannerUpdateDTO.getSortOrder() != null) {
            existingBanner.setSortOrder(bannerUpdateDTO.getSortOrder());
        }

        Banner updatedBanner = bannerRepository.save(existingBanner);
        return convertToDTO(updatedBanner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("轮播图不存在");
        }
        bannerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BannerDTO updateBannerStatus(Long id, BannerStatus status) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("轮播图不存在"));

        banner.setStatus(status);
        Banner updatedBanner = bannerRepository.save(banner);
        return convertToDTO(updatedBanner);
    }

    @Override
    @Transactional
    public void updateBannerOrder(List<Long> bannerIds) {
        List<Banner> banners = bannerRepository.findAllById(bannerIds);
        if (banners.size() != bannerIds.size()) {
            throw new ResourceNotFoundException("部分轮播图不存在");
        }

        // 更新排序
        IntStream.range(0, bannerIds.size()).forEach(i -> {
            Long bannerId = bannerIds.get(i);
            banners.stream()
                    .filter(banner -> banner.getId().equals(bannerId))
                    .findFirst()
                    .ifPresent(banner -> banner.setSortOrder(i));
        });

        bannerRepository.saveAll(banners);
    }

    @Override
    public List<BannerClientDTO> getActiveBanners() {
        List<Banner> activeBanners = bannerRepository.findActiveBanners(LocalDateTime.now());
        return activeBanners.stream()
                .map(this::convertToClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordBannerClick(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("轮播图不存在"));

        banner.setClickCount(banner.getClickCount() + 1);
        bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void recordBannerView(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("轮播图不存在"));

        banner.setViewCount(banner.getViewCount() + 1);
        bannerRepository.save(banner);
    }

    // 辅助方法：获取下一个排序值
    private Integer getNextSortOrder() {
        return bannerRepository.findAll()
                .stream()
                .mapToInt(b -> b.getSortOrder() != null ? b.getSortOrder() : 0)
                .max()
                .orElse(0) + 1;
    }

    // 辅助方法：将实体转换为DTO
    private BannerDTO convertToDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        dto.setId(banner.getId());
        dto.setTitle(banner.getTitle());
        dto.setImageUrl(banner.getImageUrl());
        dto.setLinkType(banner.getLinkType());
        dto.setLinkUrl(banner.getLinkUrl());
        dto.setLinkId(banner.getLinkId());
        dto.setLinkAppid(banner.getLinkAppid());
        dto.setLinkPage(banner.getLinkPage());
        dto.setSortOrder(banner.getSortOrder());
        dto.setStatus(banner.getStatus());
        dto.setStartTime(banner.getStartTime());
        dto.setEndTime(banner.getEndTime());
        dto.setViewCount(banner.getViewCount());
        dto.setClickCount(banner.getClickCount());
        dto.setCreatedAt(banner.getCreatedAt());
        dto.setUpdatedAt(banner.getUpdatedAt());

        if (banner.getCreatedBy() != null) {
            dto.setCreatedById(banner.getCreatedBy().getId());
            dto.setCreatedByUsername(banner.getCreatedBy().getUsername());
        }

        return dto;
    }

    // 辅助方法：将实体转换为客户端DTO
    private BannerClientDTO convertToClientDTO(Banner banner) {
        BannerClientDTO dto = new BannerClientDTO();
        dto.setId(banner.getId());
        dto.setTitle(banner.getTitle());
        dto.setImageUrl(banner.getImageUrl());
        dto.setLinkType(banner.getLinkType());
        dto.setLinkUrl(banner.getLinkUrl());
        dto.setLinkId(banner.getLinkId());
        dto.setLinkAppid(banner.getLinkAppid());
        dto.setLinkPage(banner.getLinkPage());
        return dto;
    }
}