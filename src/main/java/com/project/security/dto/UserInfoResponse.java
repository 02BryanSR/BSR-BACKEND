package com.project.security.dto;

public class UserInfoResponse {

    private String email;
    private String role;

    public UserInfoResponse(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}

