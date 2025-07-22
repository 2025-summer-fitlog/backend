package com.comwith.fitlog.users.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;  // 아이디

    @NotBlank(message = "비밀번호를 입력해주세요.")
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
    public void setPassword(String password) { this.password = password; }
}

