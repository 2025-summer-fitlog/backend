package com.comwith.fitlog.users.service;

import com.comwith.fitlog.users.entity.EmailVerification;
import com.comwith.fitlog.users.repository.EmailVerificationRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    // 검증 코드 발송
    public void sendVerificationEmail(String email) {
        // 6자리 랜덤 코드 생성
        String verificationCode = generateVerificationCode();

        // 이메일 검증 정보 저장
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setVerificationCode(verificationCode);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // 5분 유효

        emailVerificationRepository.save(verification);

        // 이메일 발송
        sendEmail(email, verificationCode);
    }

    private void sendEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("회원가입 이메일 인증");

            String htmlContent = String.format("""
                <div style='margin:20px;'>
                    <h2>이메일 인증</h2>
                    <p>안녕하세요! 회원가입을 완료하기 위해 아래 인증번호를 입력해주세요.</p>
                    <div style='background-color:#f5f5f5; padding:20px; text-align:center; margin:20px 0;'>
                        <h1 style='color:#007bff; margin:0;'>%s</h1>
                    </div>
                    <p>이 인증번호는 5분간 유효합니다.</p>
                </div>
                """, verificationCode);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    // 검증 코드 확인
    public boolean verifyEmail(String email, String code) {
        Optional<EmailVerification> verification =
                emailVerificationRepository.findByEmailAndVerificationCodeAndVerifiedFalse(email, code);

        if (verification.isPresent()) {
            EmailVerification ver = verification.get();
            if (ver.getExpiresAt().isAfter(LocalDateTime.now())) {
                ver.setVerified(true);
                emailVerificationRepository.save(ver);
                return true;
            }
        }
        return false;
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // 이메일 검증 완료 여부 확인 (UserController에서 사용)
    public boolean isEmailVerified(String email) {
        Optional<EmailVerification> verification =
                emailVerificationRepository.findByEmailAndVerifiedTrue(email);
        return verification.isPresent();
    }

    // 기존 검증 기록 삭제 (중복 발송 방지)
    @Transactional
    public void deleteExistingVerification(String email) {
        emailVerificationRepository.deleteByEmail(email);
    }
}
