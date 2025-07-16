package com.comwith.health_poc.users.dto;

public class UserLoginRequest {
    private String username;  // 아이디
    private String password;

    public UserLoginRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public UserLoginRequest(String password) {
        this.password = password;
    }
}

