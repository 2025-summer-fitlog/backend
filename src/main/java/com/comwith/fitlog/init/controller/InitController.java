package com.comwith.fitlog.init.controller;

import com.comwith.fitlog.init.dto.*;
import com.comwith.fitlog.init.entity.*;
import com.comwith.fitlog.init.service.InitService;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User; // OAuth2User 임포트
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
public class InitController {

    private final InitService initService;
    private final UserRepository userRepository;

    public InitController(InitService initService, UserRepository userRepository) {
        this.initService = initService;
        this.userRepository = userRepository;
    }

    // 사용자 ID를 안전하게 가져오는 헬퍼 메서드
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("로그인된 사용자만 접근할 수 있습니다.");
        }

        Object principal = authentication.getPrincipal();

        // 1. User 엔티티가 UserDetails를 구현한 경우 (로컬 로그인 시 주로 해당)
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        // 2. CustomOAuth2User를 사용하여 User 엔티티를 Principal에 포함한 경우 (권장되는 소셜 로그인 처리)
        //    이 경우 CustomOAuth2User 클래스를 별도로 생성해야 합니다.
        //    예: if (principal instanceof com.comwith.fitlog.config.CustomOAuth2User) {
        //           return ((com.comwith.fitlog.config.CustomOAuth2User) principal).getUser().getId();
        //        }
        // 3. Spring Security의 기본 UserDetails 객체인 경우 (UserService에서 org.springframework.security.core.userdetails.User를 반환할 때)
        else if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다: " + username));
            return user.getId();
        }
        // 4. Spring Security의 기본 OAuth2User 객체인 경우 (CustomOAuth2UserService에서 DefaultOAuth2User를 반환할 때)
        else if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            String providerId = oauth2User.getName(); // OAuth2User의 고유 ID (sub, id 등)

            // CustomOAuth2UserService에서 User 엔티티를 저장할 때 사용한 loginMethod와 providerId를 활용하여 User를 찾습니다.
            // CustomOAuth2UserService에서 kakao_account.email 또는 google의 email을 User 엔티티의 email에 저장했다면
            // email을 통해 찾는 것이 더 정확할 수 있습니다.
            String email = (String) oauth2User.getAttributes().get("email"); // 이메일 속성 가져오기 시도

            if (email != null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("소셜 로그인된 사용자를 찾을 수 없습니다: " + email));
                return user.getId();
            } else {
                // 이메일이 없는 경우, providerId와 loginMethod로 찾기 시도
                String loginMethod = (String) oauth2User.getAttributes().get("loginMethod"); // CustomOAuth2UserService에서 추가한 속성
                if (loginMethod == null) {
                    // loginMethod가 attributes에 직접 추가되지 않은 경우, registrationId를 사용
                    // 이 부분은 CustomOAuth2UserService에서 어떻게 attributes를 구성했는지에 따라 달라집니다.
                    // 예: "google", "kakao"
                    // 현재 CustomOAuth2UserService는 registrationId를 loginMethod로 사용하고 있으므로,
                    // attributes에 직접 "loginMethod"를 추가하지 않았다면 이 부분은 사용되지 않습니다.
                    // 대신, providerId (oauth2User.getName())를 username으로 사용하여 찾을 수 있습니다.
                    User user = userRepository.findByUsername(providerId) // DefaultOAuth2User의 name을 username으로 매핑했을 경우
                            .orElseThrow(() -> new IllegalArgumentException("소셜 로그인된 사용자를 찾을 수 없습니다: " + providerId));
                    return user.getId();
                } else {
                    User user = userRepository.findByLoginMethodAndProviderId(loginMethod, providerId)
                            .orElseThrow(() -> new IllegalArgumentException("소셜 로그인된 사용자를 찾을 수 없습니다: " + loginMethod + ", " + providerId));
                    return user.getId();
                }
            }
        }
        // 지원하지 않는 인증 주체 타입
        else {
            throw new IllegalArgumentException("지원하지 않는 인증 주체 타입입니다: " + principal.getClass().getName());
        }
    }


    // 신체 정보 수집
    @PostMapping("/body")
    public ResponseEntity<?> collectPhysicalInfo(@Valid @RequestBody PhysicalInfoRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication); // ✨ 헬퍼 메서드 사용

            UserPhysicalInfo savedInfo = initService.savePhysicalInfo(userId, request);
            return ResponseEntity.ok(savedInfo);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    // 운동 목적 설정
    @PostMapping("/goals")
    public ResponseEntity<?> setGoals(@Valid @RequestBody GoalRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication); // ✨ 헬퍼 메서드 사용

            List<UserGoal> savedGoals = initService.saveGoals(userId, request);
            return ResponseEntity.ok(savedGoals);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }


    // 희망 운동 시간 설정
    @PostMapping("/preferences/time")
    public ResponseEntity<?> setPreferredTime(@Valid @RequestBody PreferredTimeRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication); // ✨ 헬퍼 메서드 사용

            UserPreferredTime savedTime = initService.savePreferredTime(userId, request);
            return ResponseEntity.ok(savedTime);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    // 운동 횟수 설정
    @PostMapping("/preferences/frequency")
    public ResponseEntity<?> setWorkoutFrequency(@Valid @RequestBody WorkoutFrequencyRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication); // ✨ 헬퍼 메서드 사용

            UserWorkoutFrequency savedFrequency = initService.saveWorkoutFrequency(userId, request);
            return ResponseEntity.ok(savedFrequency);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        }
    }

    // 주요 운동 부위 설정
    @PostMapping("/parts")
    public ResponseEntity<?> setMainParts(@Valid @RequestBody MainPartRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication); // ✨ 헬퍼 메서드 사용

            List<UserMainPart> savedParts = initService.saveMainParts(userId, request);
            return ResponseEntity.ok(savedParts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}
