package com.primehub.primecardadmin.controller;

import com.primehub.primecardadmin.dto.*;
import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.security.UserSecurity;
import com.primehub.primecardadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    
    @Autowired
    private UserSecurity userSecurity;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserDTO>> register(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO userDTO = userService.register(registrationDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("用户注册成功", userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponse = userService.login(loginRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("登录成功", loginResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        LoginResponseDTO loginResponse = userService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(ApiResponseDTO.success("令牌刷新成功", loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        userService.logout(logoutRequestDTO.getToken());
        return ResponseEntity.ok(ApiResponseDTO.success("登出成功"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser() {
        User currentUser = userSecurity.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.ok(ApiResponseDTO.error("未获取到当前用户信息"));
        }
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(currentUser.getId());
        userDTO.setUsername(currentUser.getUsername());
        userDTO.setEmail(currentUser.getEmail());
        userDTO.setAvatar(currentUser.getAvatar());
        userDTO.setRole(currentUser.getRole());
        userDTO.setStatus(currentUser.getStatus());
        userDTO.setLastLoginAt(currentUser.getLastLoginAt());
        userDTO.setCreatedAt(currentUser.getCreatedAt());
        userDTO.setUpdatedAt(currentUser.getUpdatedAt());
        
        return ResponseEntity.ok(ApiResponseDTO.success("获取当前用户信息成功", userDTO));
    }
}