package com.primehub.primecardadmin.util;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/debug")
public class BannerFixController {

    @Autowired
    private BannerRepository bannerRepository;

    @PostMapping("/banners/fix-time")
    public ApiResponseDTO<String> fixBannerTime() {
        // 查询所有banner
        List<Banner> allBanners = bannerRepository.findAll();
        
        // 更新结束时间为2026年12月31日
        LocalDateTime newEndTime = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        
        int updatedCount = 0;
        for (Banner banner : allBanners) {
            banner.setEndTime(newEndTime);
            bannerRepository.save(banner);
            updatedCount++;
        }
        
        return ApiResponseDTO.success("修复完成，更新了 " + updatedCount + " 条记录的结束时间为: " + newEndTime);
    }
}