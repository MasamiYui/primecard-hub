package com.primehub.primecardadmin.service;

import com.primehub.primecardadmin.dto.*;
import com.primehub.primecardadmin.entity.User;
import com.primehub.primecardadmin.entity.UserRole;
import com.primehub.primecardadmin.entity.UserStatus;
import com.primehub.primecardadmin.exception.ResourceNotFoundException;
import com.primehub.primecardadmin.repository.UserRepository;
import com.primehub.primecardadmin.repository.UserSessionRepository;
import com.primehub.primecardadmin.service.impl.UserServiceImpl;
import com.primehub.primecardadmin.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSessionRepository userSessionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationDTO registrationDTO;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.ADMIN);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setCreatedAt(LocalDateTime.now());

        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setConfirmPassword("password123");

        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("testuser");
        loginRequestDTO.setPassword("password123");
    }

    @Test
    void register_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.register(registrationDTO);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registrationDTO);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(registrationDTO);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenUtil.generateToken(anyString())).thenReturn("mock-jwt-token");

        // When
        LoginResponseDTO result = userService.login(loginRequestDTO);

        // Then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getUser());
        assertEquals("testuser", result.getUser().getUsername());
    }

    @Test
    void findById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.findById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findAll_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDTO> result = userService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void update_Success() {
        // Given
        UserDTO updateDTO = new UserDTO();
        updateDTO.setId(1L);
        updateDTO.setUsername("updateduser");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setRole(UserRole.VIEWER);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole(UserRole.VIEWER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserDTO result = userService.update(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(UserRole.VIEWER, result.getRole());
    }

    @Test
    void update_UserNotFound_ThrowsException() {
        // Given
        UserDTO updateDTO = new UserDTO();
        updateDTO.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.update(1L, updateDTO);
        });
    }

    @Test
    void delete_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        userService.delete(1L);

        // Then
        verify(userRepository).delete(testUser);
    }

    @Test
    void delete_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.delete(1L);
        });
    }

    @Test
    void convertToDTO_Success() {
        // When
        UserDTO result = userService.convertToDTO(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getRole(), result.getRole());
        assertEquals(testUser.getStatus(), result.getStatus());
    }
}