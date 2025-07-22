// src/main/java/com/comwith/fitlog/controller/DashboardController.java
package com.comwith.fitlog.controller; // 적절한 패키지로 변경

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.comwith.fitlog.users.entity.User; // User 엔티티 import
import java.util.Map;


// 로깅을 위한 임포트 추가
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userName = "Guest"; // 기본값

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) { // 로컬 로그인 사용자
                User user = (User) principal;
                userName = user.getName(); // User 엔티티의 name 필드 사용
            } else if (principal instanceof OAuth2User) { // 소셜 로그인 사용자 (OAuth2User)
                OAuth2User oauth2User = (OAuth2User) principal;
                // OAuth2User에서 이름 가져오기. 프로바이더마다 속성 이름이 다를 수 있음.
                // Google: "name", Kakao: "properties.nickname" (CustomOAuth2UserService에서 name으로 매핑했으므로, 여기서도 "name" 시도)
                userName = oauth2User.getAttribute("name");
                if (userName == null && oauth2User.getAttributes().containsKey("properties")) {
                    Map<String, Object> properties = oauth2User.getAttribute("properties");
                    if (properties != null && properties.containsKey("nickname")) {
                        userName = (String) properties.get("nickname");
                    }
                }
                if (userName == null) { // Fallback, 없을 경우 email 사용
                    userName = oauth2User.getAttribute("email");
                }
            } else { // 기타 인증 사용자 (예: UserDetailsImpl 등)
                userName = authentication.getName(); // Spring Security UserDetails.getUsername() 반환
            }
        }

        model.addAttribute("userName", userName);
        return "user_dashboard"; // templates/user_dashboard.html 반환
    }
}