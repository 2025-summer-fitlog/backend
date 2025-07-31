package com.comwith.fitlog.profile.service;

import com.comwith.fitlog.profile.dto.ProfileUserResponse;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileUserResponse getCurrentUserProfile(String username) {
        log.info("사용자 프로필 조회 요청: {}", username);

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명이 비어있습니다.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다 :" + username));

        return ProfileUserResponse.builder()
                .name(user.getName())
                .build();
    }
}
