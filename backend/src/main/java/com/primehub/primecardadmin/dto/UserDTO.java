package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.UserRole;
import com.primehub.primecardadmin.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "用户信息DTO")
public class UserDTO {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin", required = true)
    private String username;
    
    @Schema(description = "邮箱地址", example = "admin@example.com", required = true)
    private String email;
    
    @Schema(description = "用户角色", example = "ADMIN")
    private UserRole role;
    
    @Schema(description = "用户状态", example = "ACTIVE")
    private UserStatus status;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, UserRole role, UserStatus status, 
                   String avatar, LocalDateTime lastLoginAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.avatar = avatar;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class UserDTOBuilder {
        private Long id;
        private String username;
        private String email;
        private UserRole role;
        private UserStatus status;
        private String avatar;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserDTOBuilder id(Long id) { this.id = id; return this; }
        public UserDTOBuilder username(String username) { this.username = username; return this; }
        public UserDTOBuilder email(String email) { this.email = email; return this; }
        public UserDTOBuilder role(UserRole role) { this.role = role; return this; }
        public UserDTOBuilder status(UserStatus status) { this.status = status; return this; }
        public UserDTOBuilder avatar(String avatar) { this.avatar = avatar; return this; }
        public UserDTOBuilder lastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }
        public UserDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserDTOBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserDTO build() {
            return new UserDTO(id, username, email, role, status, avatar, lastLoginAt, createdAt, updatedAt);
        }
    }
}