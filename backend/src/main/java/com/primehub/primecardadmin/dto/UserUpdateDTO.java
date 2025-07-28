package com.primehub.primecardadmin.dto;

import com.primehub.primecardadmin.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须是4-20个字符，只能包含字母、数字和下划线")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String avatar;
    
    private UserStatus status;
}