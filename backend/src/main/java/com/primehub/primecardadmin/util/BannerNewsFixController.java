package com.primehub.primecardadmin.util;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.entity.Banner;
import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/debug")
public class BannerNewsFixController {

    @Autowired
    private BannerRepository bannerRepository;

    @PostMapping("/banners/fix-relations")
    public ApiResponseDTO<String> fixBannerNewsRelations() {
        StringBuilder result = new StringBuilder();
        int fixedCount = 0;

        try {
            // 修复Banner ID 34，将其关联到新闻ID 20
            Optional<Banner> banner34 = bannerRepository.findById(34L);
            if (banner34.isPresent() && "如何提高信用卡额度".equals(banner34.get().getTitle())) {
                Banner b = banner34.get();
                b.setLinkType(BannerLinkType.NEWS);
                b.setLinkId(20L);
                b.setLinkUrl(null);
                bannerRepository.save(b);
                result.append("✅ 修复Banner ID 34: ").append(b.getTitle()).append(" -> 关联到新闻ID 20\n");
                fixedCount++;
            }

            // 为Banner ID 31添加新闻关联
            Optional<Banner> banner31 = bannerRepository.findById(31L);
            if (banner31.isPresent() && "招商银行信用卡专享优惠".equals(banner31.get().getTitle())) {
                Banner b = banner31.get();
                b.setLinkType(BannerLinkType.NEWS);
                b.setLinkId(22L);
                b.setLinkUrl(null);
                bannerRepository.save(b);
                result.append("✅ 修复Banner ID 31: ").append(b.getTitle()).append(" -> 关联到新闻ID 22\n");
                fixedCount++;
            }

            result.append("\n📊 修复完成统计:\n");
            result.append("- 总计修复: ").append(fixedCount).append(" 个banner\n");
            result.append("- 修复时间: ").append(LocalDateTime.now()).append("\n");

            return ApiResponseDTO.success("Banner-News关联关系修复完成", result.toString());

        } catch (Exception e) {
            return ApiResponseDTO.error("修复过程中发生错误: " + e.getMessage());
        }
    }
}