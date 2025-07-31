package com.comwith.fitlog.profile.controller;

import com.comwith.fitlog.init.dto.*;
import com.comwith.fitlog.init.service.InitService;
import com.comwith.fitlog.profile.dto.ProfilePersonalInfoRequest;
import com.comwith.fitlog.profile.dto.ProfilePersonalInfoResponse;
import com.comwith.fitlog.profile.dto.ProfileUserResponse;
import com.comwith.fitlog.profile.dto.WorkoutInfoResponseDto;
import com.comwith.fitlog.profile.service.ProfileService;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;
    private final InitService initService;
    private final UserRepository userRepository;

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
            // 통합 조회 로직 사용 (소셜/로컬 로그인 모두 지원)
            User user = profileService.findUserByAuthentication(authentication);

            ProfileUserResponse response = ProfileUserResponse.builder()
                    .name(user.getName())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생: '{}'", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "사용자를 찾을 수 없습니다: " + (e.getMessage() != null ? e.getMessage() : "알 수 없는 오류"));
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); 
            // UNAUTHORIZED -> NOT_FOUND 변경
        }
    }
    
    // 개인정보 조회
    @GetMapping("/personal-info")
    public ResponseEntity<?> getPersonalInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            ProfilePersonalInfoResponse response = profileService.getPersonalInfo(authentication);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("개인정보 조회 중 오류 발생: '{}'", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "개인정보를 조회할 수 없습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 개인정보 수정
    @PutMapping("/personal-info")
    public ResponseEntity<?> updatePersonalInfo(Authentication authentication, @Valid @RequestBody ProfilePersonalInfoRequest request) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            ProfilePersonalInfoResponse response = profileService.updatePersonalInfo(authentication, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("개인정보 수정 중 오류 발생: '{}'", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "개인정보를 수정할 수 없습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /* ------------------ 운동정보 조회 및 수정 ---------------- */
    /**
     * 전체 운동정보 조회
     */
    @GetMapping("/workout-info")
    public ResponseEntity<WorkoutInfoResponseDto> getWorkoutInfo(Authentication auth) {
        log.info("운동정보 조회 API 호출 - 사용자: {}", auth.getName());

        // Service 레이어에 위임
        WorkoutInfoResponseDto response = profileService.getWorkoutInfo(auth);

        log.info("운동정보 조회 API 완료 - 사용자: {}", auth.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * 신체정보 수정
     */
    @PutMapping("/workout-info/body")
    public ResponseEntity<Void> updateBodyInfo(@RequestBody PhysicalInfoRequest dto, Authentication auth) {
        log.info("신체정보 수정 API 호출 - 사용자: {}", auth.getName());

        String identifier = auth.getName();
        User user = profileService.findUserByUsernameOrProviderId(identifier);

        initService.savePhysicalInfo(user.getId(), dto);

        log.info("신체정보 수정 완료 - 사용자: {}", auth.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 운동목적 수정
     */
    @PutMapping("/workout-info/goals")
    public ResponseEntity<Void> updateWorkoutGoals(@RequestBody GoalRequest dto, Authentication auth) {
        log.info("운동목적 수정 API 호출 - 사용자: {}", auth.getName());

        String identifier = auth.getName();
        User user = profileService.findUserByUsernameOrProviderId(identifier);

        initService.saveGoals(user.getId(), dto);

        log.info("운동목적 수정 완료 - 사용자: {}", auth.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 선호시간 수정
     */
    @PutMapping("/workout-info/preferred-time")
    public ResponseEntity<Void> updatePreferredTime(@RequestBody PreferredTimeRequest dto, Authentication auth) {
        log.info("선호시간 수정 API 호출 - 사용자: {}", auth.getName());

        String identifier = auth.getName();
        User user = profileService.findUserByUsernameOrProviderId(identifier);

        initService.savePreferredTime(user.getId(), dto);

        log.info("선호시간 수정 완료 - 사용자: {}", auth.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 운동빈도 수정
     */
    @PutMapping("/workout-info/frequency")
    public ResponseEntity<Void> updateWorkoutFrequency(@RequestBody WorkoutFrequencyRequest dto, Authentication auth) {
        log.info("운동빈도 수정 API 호출 - 사용자: {}", auth.getName());

        String identifier = auth.getName();
        User user = profileService.findUserByUsernameOrProviderId(identifier);

        initService.saveWorkoutFrequency(user.getId(), dto);

        log.info("운동빈도 수정 완료 - 사용자: {}", auth.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 주요운동부위 수정
     */
    @PutMapping("/workout-info/main-parts")
    public ResponseEntity<Void> updateMainParts(@RequestBody MainPartRequest dto, Authentication auth) {
        log.info("주요운동부위 수정 API 호출 - 사용자: {}", auth.getName());

        String identifier = auth.getName();
        User user = profileService.findUserByUsernameOrProviderId(identifier);

        initService.saveMainParts(user.getId(), dto);

        log.info("주요운동부위 수정 완료 - 사용자: {}", auth.getName());
        return ResponseEntity.ok().build();
    }

}
