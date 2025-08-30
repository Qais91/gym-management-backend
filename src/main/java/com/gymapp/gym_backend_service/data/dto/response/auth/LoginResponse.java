package com.gymapp.gym_backend_service.data.dto.response.auth;

public class LoginResponse {
    private String username;
    private String role;
    private String token;

    public LoginResponse(String username, String role, String token) {
        this.username = username;
        this.token = token;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}