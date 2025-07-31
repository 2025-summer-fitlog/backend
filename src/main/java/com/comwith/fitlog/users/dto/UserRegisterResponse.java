package com.comwith.fitlog.users.dto;

public class UserRegisterResponse {
    private Long id;
    private String username;
    private String name;
    private String email;


    // 기본 생성자
    public UserRegisterResponse() {}

    public UserRegisterResponse(Long id, String username, String name, String email) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }
}
