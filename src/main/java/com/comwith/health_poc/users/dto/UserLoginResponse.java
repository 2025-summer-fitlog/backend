package com.comwith.health_poc.users.dto;

public class UserLoginResponse {

    private Long id;
    private String username;
    private String nickname;

    // 기본 생성자
    public UserLoginResponse() {}

    public UserLoginResponse(Long id, String username, String nickname) {

        this.id = id;
        this.username = username;
        this.nickname = nickname;

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }
}


