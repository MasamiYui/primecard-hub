package com.primehub.primecardadmin.security;

import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    /**
     * 检查当前登录用户是否是指定的用户ID
     * 用于Spring Security的@PreAuthorize注解中
     *
     * @param userId 要检查的用户ID
     * @return 如果当前用户是指定的用户，则返回true
     */
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户，如果未登录则返回null
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}