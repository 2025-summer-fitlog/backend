package com.comwith.fitlog.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping; // GET 요청 처리

@Controller // 뷰를 반환하는 컨트롤러
public class LogoutController {

    // Spring Security의 로그아웃 핸들러 인스턴스
    private SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    /**
     * 사용자 정의 로그아웃을 처리하는 GET 엔드포인트입니다.
     * 일반적으로 로그아웃은 CSRF 보호를 위해 POST 요청으로 처리하는 것이 보안상 더 권장됩니다.
     * 하지만 사용자의 요청에 따라 GET 방식으로 구현되었습니다.
     *
     * @param request 현재 HTTP 서블릿 요청
     * @param response 현재 HTTP 서블릿 응답
     * @param authentication 현재 인증된 사용자 정보
     * @return 로그아웃 성공 후 리다이렉션할 URL
     */
    @GetMapping("/custom-logout") // 이 URL로 GET 요청이 오면 로그아웃 처리
    public String performLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 현재 인증 정보가 존재하면 로그아웃 처리
        if (authentication != null) {
            this.logoutHandler.logout(request, response, authentication);
        }
        // 로그아웃 후 로그인 페이지로 리다이렉트하며, 'logout' 파라미터를 추가하여 메시지 표시
        return "redirect:/login_test?logout";
    }
}