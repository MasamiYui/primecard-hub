package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.ApiResponseDTO;
import com.primehub.primecardadmin.dto.PageResponseDTO;
import com.primehub.primecardadmin.dto.UserDTO;
import com.primehub.primecardadmin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "用户管理", description = "用户相关的API接口")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持关键词搜索")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<UserDTO>>> getAllUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage = userService.findAll(pageable);
        
        PageResponseDTO<UserDTO> users = new PageResponseDTO<>();
        users.setContent(userPage.getContent());
        users.setTotalElements(userPage.getTotalElements());
        users.setTotalPages(userPage.getTotalPages());
        users.setPageNumber(page);
        users.setPageSize(size);
        users.setFirst(userPage.isFirst());
        users.setLast(userPage.isLast());
        
        return ResponseEntity.ok(ApiResponseDTO.success("获取用户列表成功", users));
    }

    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        Optional<UserDTO> userDTO = userService.findById(id);
        if (userDTO.isPresent()) {
            return ResponseEntity.ok(ApiResponseDTO.success("获取用户信息成功", userDTO.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.update(id, userDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("用户信息更新成功", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.success("用户删除成功"));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        boolean success = userService.changePassword(id, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok(ApiResponseDTO.success("密码修改成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponseDTO.error("密码修改失败"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser() {
        // 暂时返回空用户，需要从Security Context获取当前用户
        return ResponseEntity.ok(ApiResponseDTO.success("获取当前用户信息成功", new UserDTO()));
    }
}