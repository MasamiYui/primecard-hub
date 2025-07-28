package com.primehub.primecardadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDTO {
    
    @NotBlank(message = "令牌不能为空")
    private String token;
}