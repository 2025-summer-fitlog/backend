package com.comwith.fitlog.profile.controller;

import com.comwith.fitlog.profile.dto.ProfileUserResponse;
import com.comwith.fitlog.profile.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser (Authentication authentication, HttpServletRequest request) {


        // 인증 확인
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            log.warn("미인증된 사용자의 프로필 접근 시도를 차단합니다.");
            Map<String, String> error = new HashMap<>();
            error.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            String username = authentication.getName();
            log.info("인증된 사용자: {}", username);
            ProfileUserResponse response = profileService.getCurrentUserProfile(username);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "사용자를 찾을 수 없습니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
