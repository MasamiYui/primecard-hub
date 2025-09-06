package com.primehub.primecardadmin.service.client.impl;

import com.primehub.primecardadmin.dto.client.BannerClientDTO;
import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.BannerRepository;
import com.primehub.primecardadmin.service.client.BannerClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerClientServiceImpl implements BannerClientService {

    @Autowired
    private BannerRepository bannerRepository;

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