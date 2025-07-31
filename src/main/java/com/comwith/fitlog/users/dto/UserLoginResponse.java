package com.comwith.fitlog.users.dto;

public class UserLoginResponse {

    private Long id;
    private String username;
    private String name;

    // 기본 생성자
    public UserLoginResponse() {}

    public UserLoginResponse(Long id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}


