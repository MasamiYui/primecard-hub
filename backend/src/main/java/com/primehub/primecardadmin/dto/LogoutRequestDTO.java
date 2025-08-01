package com.primehub.primecardadmin.dto;

import javax.validation.constraints.NotBlank;

public class LogoutRequestDTO {
    
    @NotBlank(message = "令牌不能为空")
    private String token;

    // 构造函数
    public LogoutRequestDTO() {}

    public LogoutRequestDTO(String token) {
        this.token = token;
    }

    // Getter和Setter方法
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}