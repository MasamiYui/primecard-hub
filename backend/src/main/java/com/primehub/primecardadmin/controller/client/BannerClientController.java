package com.primehub.primecardadmin.controller.client;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.client.BannerClientDTO;
import com.primehub.primecardadmin.service.client.BannerClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/banners")
@Tag(name = "C端轮播图", description = "C端轮播图接口")
public class BannerClientController {

    @Autowired
    private BannerClientService bannerClientService;

    @Operation(summary = "获取活跃轮播图", description = "获取当前活跃的轮播图列表，用于C端展示")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<BannerClientDTO>>> getActiveBanners() {
        List<BannerClientDTO> banners = bannerClientService.getActiveBanners();
        return ResponseEntity.ok(ApiResponseDTO.success("获取轮播图成功", banners));
    }

    @Operation(summary = "记录轮播图点击", description = "记录用户点击轮播图的行为")
    @PostMapping("/{id}/click")
    public ResponseEntity<ApiResponseDTO<Void>> recordBannerClick(
            @Parameter(description = "轮播图ID") @PathVariable Long id) {
        bannerClientService.recordBannerClick(id);
        return ResponseEntity.ok(ApiResponseDTO.success("记录点击成功"));
    }

    @Operation(summary = "记录轮播图浏览", description = "记录用户浏览轮播图的行为")
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponseDTO<Void>> recordBannerView(
            @Parameter(description = "轮播图ID") @PathVariable Long id) {
        bannerClientService.recordBannerView(id);
        return ResponseEntity.ok(ApiResponseDTO.success("记录浏览成功"));
    }
}