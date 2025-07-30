package com.comwith.fitlog.users.repository;


import com.comwith.fitlog.users.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 이메일과 코드로 미검증 상태인 레코드 찾기
    Optional<EmailVerification> findByEmailAndVerificationCodeAndVerifiedFalse(String email, String verificationCode);

    // 특정 이메일의 검증 완료된 레코드 찾기
    Optional<EmailVerification> findByEmailAndVerifiedTrue(String email);

    // 이메일로 모든 레코드 삭제 (중복 방지용)
    void deleteByEmail(String email);
}
