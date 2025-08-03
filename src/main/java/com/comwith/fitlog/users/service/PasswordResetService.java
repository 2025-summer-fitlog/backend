package com.comwith.fitlog.users.service;


import com.comwith.fitlog.users.entity.ResetPasswordToken;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.ResetPasswordTokenRepository;
import com.comwith.fitlog.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired private UserRepository userRepository;
    @Autowired private ResetPasswordTokenRepository tokenRepository;
    @Autowired private JavaMailSender mailSender;

    // 1. 토큰 생성 & 이메일 발송
    @Transactional
    public boolean createPasswordResetToken(String username, String name, String email) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        if (!user.getEmail().equals(email) || !user.getName().equals(name)) return false;

        // 이전 토큰 정리
        tokenRepository.findAll().stream()
                .filter(t -> t.getEmail().equals(email))
                .forEach(t -> tokenRepository.deleteById(t.getId()));

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        ResetPasswordToken resetToken = new ResetPasswordToken(
                token, email, now, now.plusMinutes(30)
        );
        tokenRepository.save(resetToken);

        sendResetEmail(email, token); // 메일 발송
        return true;
    }

    private void sendResetEmail(String toEmail, String token) {
        String link = "https://fitlog-2025.duckdns.org/password-reset?token=" + token;
        String html = "<p>비밀번호 재설정은 아래 링크에서 30분 이내 완료하세요.</p>"
                + "<a href='" + link + "'>비밀번호 재설정하기</a>";
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[FITLOG] 비밀번호 재설정");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) { throw new RuntimeException("이메일 전송 실패", e); }
    }

    // 2. 토큰 유효성 검증
    public boolean isValidToken(String token) {
        Optional<ResetPasswordToken> t = tokenRepository.findByToken(token);
        return t.isPresent() && t.get().getExpiresAt().isAfter(LocalDateTime.now());
    }

    // 3. 비밀번호 재설정
    @Transactional
    public boolean resetPassword(String token, String newPassword, BCryptPasswordEncoder encoder) {
        Optional<ResetPasswordToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) return false;
        ResetPasswordToken t = tokenOpt.get();
        if (t.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        Optional<User> userOpt = userRepository.findByEmail(t.getEmail());
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.deleteByToken(token); // 토큰 1회성, 즉시 삭제
        return true;
    }
}
