package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.BannerCreateDTO;
import com.primehub.primecardadmin.dto.BannerDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.entity.BannerLinkType;
import com.primehub.primecardadmin.entity.BannerStatus;
import com.primehub.primecardadmin.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/banners")
@Tag(name = "轮播图管理", description = "轮播图的增删改查接口")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Operation(summary = "获取轮播图列表", description = "分页获取轮播图列表，支持多种筛选条件")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<BannerDTO>>> getAllBanners(
            @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) BannerStatus status,
            @Parameter(description = "链接类型筛选") @RequestParam(required = false) BannerLinkType linkType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        PageResponseDTO<BannerDTO> banners = bannerService.getAllBanners(page, size, keyword, status, linkType, startDate, endDate);
        return ResponseEntity.ok(ApiResponseDTO.success("获取轮播图列表成功", banners));
    }

    @Operation(summary = "根据ID获取轮播图", description = "根据ID获取轮播图详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<BannerDTO>> getBannerById(
            @Parameter(description = "轮播图ID") @PathVariable Long id) {

        BannerDTO banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(ApiResponseDTO.success("获取轮播图详情成功", banner));
    }

    @Operation(summary = "创建轮播图", description = "创建新的轮播图")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<BannerDTO>> createBanner(
            @Parameter(description = "轮播图信息") @Valid @RequestBody BannerCreateDTO bannerCreateDTO) {

        BannerDTO createdBanner = bannerService.createBanner(bannerCreateDTO);
        return new ResponseEntity<>(ApiResponseDTO.success("创建轮播图成功", createdBanner), HttpStatus.CREATED);
    }

    @Operation(summary = "更新轮播图", description = "根据ID更新轮播图信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<BannerDTO>> updateBanner(
            @Parameter(description = "轮播图ID") @PathVariable Long id,
            @Parameter(description = "轮播图信息") @Valid @RequestBody BannerCreateDTO bannerUpdateDTO) {

        BannerDTO updatedBanner = bannerService.updateBanner(id, bannerUpdateDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("更新轮播图成功", updatedBanner));
    }

    @Operation(summary = "删除轮播图", description = "根据ID删除轮播图")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteBanner(
            @Parameter(description = "轮播图ID") @PathVariable Long id) {

        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponseDTO.success("删除轮播图成功"));
    }

    @Operation(summary = "更新轮播图状态", description = "根据ID更新轮播图状态")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<BannerDTO>> updateBannerStatus(
            @Parameter(description = "轮播图ID") @PathVariable Long id,
            @Parameter(description = "状态信息") @RequestBody Map<String, BannerStatus> statusMap) {

        BannerStatus status = statusMap.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(ApiResponseDTO.error("状态不能为空"));
        }

        BannerDTO updatedBanner = bannerService.updateBannerStatus(id, status);
        return ResponseEntity.ok(ApiResponseDTO.success("更新轮播图状态成功", updatedBanner));
    }

    @Operation(summary = "调整轮播图顺序", description = "调整轮播图的显示顺序")
    @PutMapping("/order")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponseDTO<Void>> updateBannerOrder(
            @Parameter(description = "轮播图ID列表，按照显示顺序排列") @RequestBody List<Long> bannerIds) {

        bannerService.updateBannerOrder(bannerIds);
        return ResponseEntity.ok(ApiResponseDTO.success("调整轮播图顺序成功"));
    }
}