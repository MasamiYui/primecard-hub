package com.primehub.primecardadmin.dto;

import javax.validation.constraints.NotBlank;

public class RefreshTokenRequestDTO {
    
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;

    // 构造函数
    public RefreshTokenRequestDTO() {}

    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getter和Setter方法
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}