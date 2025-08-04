package com.comwith.fitlog.backend.Dto;

import java.util.List;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private List<Long> savedVideoIds;

    public UserDto() {}

    public UserDto(Long id, String username, String email, List<Long> savedVideoIds){
        this.id = id;
        this.username = username;
        this.email = email;
        this.savedVideoIds = savedVideoIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getSavedVideoIds() {
        return savedVideoIds;
    }

    public void setSavedVideoIds(List<Long> savedVideoIds) {
        this.savedVideoIds = savedVideoIds;
    }
}
