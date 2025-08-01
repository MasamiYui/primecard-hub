package com.primehub.primecardadmin.service.impl;

import com.primehub.primecardadmin.dto.LoginRequestDTO;
import com.primehub.primecardadmin.dto.LoginResponseDTO;
import com.primehub.primecardadmin.dto.UserDTO;
import com.primehub.primecardadmin.dto.UserRegistrationDTO;
import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.entity.UserRole;
import com.primehub.primecardadmin.entity.UserSession;
import com.primehub.primecardadmin.entity.UserStatus;
import com.primehub.primecardadmin.repository.UserRepository;
import com.primehub.primecardadmin.repository.UserSessionRepository;
import com.primehub.primecardadmin.service.UserService;
import com.primehub.primecardadmin.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Override
    @Transactional
    public UserDTO register(UserRegistrationDTO registrationDTO) {
        // 验证密码是否一致
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(UserRole.EDITOR); // 默认为编辑角色
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // 查找用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtTokenUtil.generateToken(user.getUsername());
        String refreshToken = UUID.randomUUID().toString();
        
        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // 创建会话
        LocalDateTime expiresAt = LocalDateTime.now().plusNanos(jwtExpiration * 1_000_000);
        UserSession session = new UserSession(
                UUID.randomUUID().toString(),
                user,
                token,
                refreshToken,
                expiresAt,
                LocalDateTime.now()
        );
        
        userSessionRepository.save(session);
        
        // 返回登录响应
        return LoginResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(convertToDTO(user))
                .expiresIn(jwtExpiration / 1000)
                .build();
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDTO);
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 更新用户信息
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());
        user.setAvatar(userDTO.getAvatar());
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 删除用户会话
        userSessionRepository.deleteByUser(user);
        
        // 删除用户
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public LoginResponseDTO refreshToken(String refreshToken) {
        UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("无效的刷新令牌"));
        
        // 检查会话是否过期
        if (session.isExpired()) {
            throw new IllegalArgumentException("会话已过期");
        }
        
        // 生成新的Token
        String newToken = jwtTokenUtil.generateToken(session.getUser().getUsername());
        String newRefreshToken = UUID.randomUUID().toString();
        LocalDateTime newExpiresAt = LocalDateTime.now().plusNanos(jwtExpiration * 1_000_000);
        
        // 更新会话
        session.refresh(newToken, newRefreshToken, newExpiresAt);
        userSessionRepository.save(session);
        
        // 返回新的登录响应
        return LoginResponseDTO.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .user(convertToDTO(session.getUser()))
                .expiresIn(jwtExpiration / 1000)
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        userSessionRepository.findByToken(token).ifPresent(session -> {
            session.invalidate();
            userSessionRepository.save(session);
        });
    }

    @Override
    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .avatar(user.getAvatar())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}