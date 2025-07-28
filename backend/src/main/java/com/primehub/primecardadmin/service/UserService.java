package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.LoginRequestDTO;
import com.primehub.primecardadmin.dto.LoginResponseDTO;
import com.primehub.primecardadmin.dto.UserDTO;
import com.primehub.primecardadmin.dto.UserRegistrationDTO;
import com.primehub.primecardadmin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    
    /**
     * 用户注册
     * @param registrationDTO 注册信息
     * @return 注册成功的用户信息
     */
    UserDTO register(UserRegistrationDTO registrationDTO);
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应，包含token和用户信息
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<UserDTO> findById(Long id);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<UserDTO> findByUsername(String username);
    
    /**
     * 分页查询所有用户
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<UserDTO> findAll(Pageable pageable);
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 用户信息
     * @return 更新后的用户信息
     */
    UserDTO update(Long id, UserDTO userDTO);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void delete(Long id);
    
    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 刷新Token
     * @param refreshToken 刷新Token
     * @return 新的登录响应
     */
    LoginResponseDTO refreshToken(String refreshToken);
    
    /**
     * 登出
     * @param token 用户Token
     */
    void logout(String token);
    
    /**
     * 将User实体转换为UserDTO
     * @param user 用户实体
     * @return 用户DTO
     */
    UserDTO convertToDTO(User user);
}