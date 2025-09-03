package com.primehub.primecardadmin.security;

import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("加载用户详情 - 用户名: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("用户不存在 - 用户名: {}", username);
                    return new UsernameNotFoundException("用户不存在: " + username);
                });

        // 检查用户状态
        if (!user.isActive()) {
            logger.warn("用户已被禁用 - 用户名: {}, ID: {}", username, user.getId());
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 创建授权列表
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        logger.debug("用户详情加载成功 - 用户名: {}, 角色: {}", username, user.getRole());
        
        // 返回Spring Security的UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}