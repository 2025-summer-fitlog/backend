// src/main/java/com/comwith/fitlog/users/entity/User.java
package com.comwith.fitlog.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String email;

    @Column(unique = true, nullable = true)
    private String username; // 아이디

    @Column(nullable = true)
    private String password;

    @Column(nullable = false) // 'nickname' 대신 'name' 사용
    private String name; // 'nickname' -> 'name'으로 변경

    // 소셜 로그인 관련 필드
    @Column(nullable = false)
    private String loginMethod;

    @Column(nullable = true)
    private String providerId;

    @Builder
    public User(Long id, String email, String username, String password, String name, String loginMethod, String providerId) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name; // 'nickname' 대신 'name' 사용
        this.loginMethod = loginMethod;
        this.providerId = providerId;
    }
}

