package com.primehub.primecardadmin.repository;

import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    
    List<UserSession> findByUser(User user);
    
    Optional<UserSession> findByToken(String token);
    
    Optional<UserSession> findByRefreshToken(String refreshToken);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByUser(User user);
}