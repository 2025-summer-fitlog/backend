package com.comwith.fitlog.users.controller;

// 비밀번호 설정 메일 링크 오류 수정을 위한 새 파일 추가
import com.comwith.fitlog.users.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetWebController {

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        // 토큰 검증은 React에서 API로 처리하므로 단순히 React 앱만 반환
        return "forward:/index.html";
    }

    // 혹시 다른 경로도 사용한다면
    @GetMapping("/resetPw")
    public String resetPwPage() {
        return "forward:/index.html";
    }
}
