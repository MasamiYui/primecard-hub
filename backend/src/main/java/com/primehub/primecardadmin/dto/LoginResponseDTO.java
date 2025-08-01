package com.primehub.primecardadmin.dto;

public class LoginResponseDTO {
    private String token;
    private String refreshToken;
    private UserDTO user;
    private long expiresIn;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String refreshToken, UserDTO user, long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
        this.expiresIn = expiresIn;
    }

    public static LoginResponseDTOBuilder builder() {
        return new LoginResponseDTOBuilder();
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public static class LoginResponseDTOBuilder {
        private String token;
        private String refreshToken;
        private UserDTO user;
        private long expiresIn;

        public LoginResponseDTOBuilder token(String token) { this.token = token; return this; }
        public LoginResponseDTOBuilder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public LoginResponseDTOBuilder user(UserDTO user) { this.user = user; return this; }
        public LoginResponseDTOBuilder expiresIn(long expiresIn) { this.expiresIn = expiresIn; return this; }

        public LoginResponseDTO build() {
            return new LoginResponseDTO(token, refreshToken, user, expiresIn);
        }
    }
}