package com.comwith.fitlog.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        ResponseEntity<?> result;
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("authenticated", false);
            result = ResponseEntity.ok(response);
        } else {
            response.put("authenticated", true);
            Map<String, Object> user = new HashMap<>();
            if (authentication instanceof OAuth2AuthenticationToken) {
                // 소셜 로그인 사용자 정보 처리
                OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                OAuth2User oauth2User = oauth2Token.getPrincipal();

                user.put("name", oauth2User.getAttribute("name"));
                user.put("provider", oauth2Token.getAuthorizedClientRegistrationId());

                // 카카오와 구글에 따른 정보 처리
                if ("kakao".equals(oauth2Token.getAuthorizedClientRegistrationId())) {
                    Map<String, Object> properties = oauth2User.getAttribute("properties");
                    if (properties != null) {
                        user.put("name", properties.get("nickname"));
                        user.put("profileImage", properties.get("profile_image"));
                    }

                    Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
                    if (kakaoAccount != null && kakaoAccount.get("email") != null) {
                        user.put("email", kakaoAccount.get("email"));
                    }
                } else if ("google".equals(oauth2Token.getAuthorizedClientRegistrationId())) {
                    user.put("email", oauth2User.getAttribute("email"));
                    user.put("profileImage", oauth2User.getAttribute("picture"));
                }

            } else {
                // 일반 로그인 사용자 정보 처리 (필요시)
                user.put("name", authentication.getName());
                user.put("provider", null);
            }
            response.put("user", user);
            result = ResponseEntity.ok(response);
        }

        return result;
    }
}
