package com.comwith.fitlog.users.controller;

import com.comwith.fitlog.users.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class PasswordResetController {

    @Autowired private PasswordResetService passwordResetService;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 1) 비번 재설정 요청 (아이디, 이름, 이메일 입력)
    @PostMapping("/request-reset-password")
    public ResponseEntity<Map<String, Object>> requestReset(@RequestBody Map<String, String> req) {
        boolean success = passwordResetService.createPasswordResetToken(
                req.get("username"), req.get("name"), req.get("email"));
        Map<String, Object> res = new HashMap<>();
        if (success) { res.put("message", "비밀번호 재설정 링크를 이메일로 발송했습니다."); return ResponseEntity.ok(res);}
        else { res.put("error", "일치하는 사용자가 없습니다."); return ResponseEntity.badRequest().body(res);}
    }

    // 2) 토큰 유효성 확인 (프론트가 링크로 진입할 때)
    @GetMapping("/check-reset-token")
    public ResponseEntity<Map<String, Object>> checkToken(@RequestParam String token) {
        Map<String, Object> res = new HashMap<>();
        if (passwordResetService.isValidToken(token)) {
            res.put("message", "토큰이 유효합니다.");
            return ResponseEntity.ok(res);
        } else {
            res.put("error", "유효하지 않거나 만료된 토큰입니다.");
            return ResponseEntity.badRequest().body(res);
        }
    }

    // 3) 실제 비번 변경
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPw(@RequestBody Map<String, String> req) {
        boolean ok = passwordResetService.resetPassword(
                req.get("token"), req.get("newPassword"), bCryptPasswordEncoder);
        Map<String, Object> res = new HashMap<>();
        if (ok) { res.put("message", "비밀번호가 성공적으로 변경됨."); return ResponseEntity.ok(res);}
        else { res.put("error", "비밀번호 변경 실패(토큰 오류/만료)."); return ResponseEntity.badRequest().body(res);}
    }
}
