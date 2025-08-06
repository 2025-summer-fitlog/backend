package com.comwith.fitlog.users.controller;

// 비밀번호 설정 메일 링크 오류 수정을 위한 새 파일 추가
import com.comwith.fitlog.users.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetWebController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/reset-password") // 404 오류 해결 위해 추가
    public String handleResetLink(@RequestParam("token") String token) {

        if (passwordResetService.isValidToken(token)) {
            // 프론트엔드 페이지 리다이렉트 (경로 확인 필요합니다)
            return "redirect:/resetPw?token=" + token;
        } else {
            return "redirect:/login?error=invalidToken";
        }
    }
}
