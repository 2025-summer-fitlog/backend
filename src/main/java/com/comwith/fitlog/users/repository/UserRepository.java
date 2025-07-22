package com.comwith.fitlog.users.repository;

import com.comwith.fitlog.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // 소셜 로그인 사용자를 찾기 위한 메서드 변경
    // authType과 providerId를 통해 유니크한 소셜 사용자 식별
    Optional<User> findByLoginMethodAndProviderId(String loginMethod, String providerId);
}
