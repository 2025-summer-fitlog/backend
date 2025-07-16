package com.comwith.health_poc.users.dto;

public class UserRegisterRequest {
    private String email;
    private String username;  // 아이디
    private String password;
    private String nickname;

    // 반드시 기본 생성자 추가!
    public UserRegisterRequest() {}

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
