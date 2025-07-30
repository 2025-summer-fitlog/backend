package com.comwith.fitlog.users.controller;

import com.comwith.fitlog.users.dto.*;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.service.EmailService;
import com.comwith.fitlog.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    // 회원가입 (전)
    /*
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            User saved = userService.save(request);

            UserRegisterResponse response = new UserRegisterResponse(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getName(),
                    saved.getEmail()
            );

            return ResponseEntity.ok(response);


        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }

    }

     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // ✅ 이메일 검증 여부 확인 추가
            if (!emailService.isEmailVerified(request.getEmail())) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "이메일 인증이 필요합니다.");
                return ResponseEntity.badRequest().body(error);
            }

            User saved = userService.save(request);

            UserRegisterResponse response = new UserRegisterResponse(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getName(),
                    saved.getEmail()
            );
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("user", response);

            return ResponseEntity.ok(responseMap);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 로그인 (Spring Security와 연동)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request, BindingResult bindingResult, HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Spring Security를 통한 인증
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            // SecurityContext를 세션에 저장한다.
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Session에 SecurityContext 저장하기
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            // 인증 성공 시 사용자 정보 반환
            User loginUser = userService.findByUsername(request.getUsername());
            UserLoginResponse response = new UserLoginResponse(
                    loginUser.getId(),
                    loginUser.getUsername(),
                    loginUser.getName()
            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            // 현재 세션 무효화
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // SecurityContext 초기화
            SecurityContextHolder.clearContext();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그아웃이 완료되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "로그아웃 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    // 현재 로그인된 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication, HttpServletRequest request) {
        System.out.println("=== /me 엔드포인트 디버깅 ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication name: " + (authentication != null ? authentication.getName() : "null"));
        System.out.println("Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "false"));
        System.out.println("Session ID: " + request.getSession().getId());

        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            Map<String, String> errors = new HashMap<>();
            errors.put("error", "로그인이 필요합니다!");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
        }

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getEmail()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    // 이메일 검증 코드 발송
    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            // 이메일 중복 체크
            if (userService.isEmailExists(email)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "이미 사용 중인 이메일입니다.");
                return ResponseEntity.badRequest().body(error);
            }

            // 기존 검증 기록 삭제 후 새로 발송
            emailService.deleteExistingVerification(email);
            emailService.sendVerificationEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "인증번호가 발송되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "이메일 발송 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // 이메일 검증 확인
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isVerified = emailService.verifyEmail(email, code);

        Map<String, Object> response = new HashMap<>();
        if (isVerified) {
            response.put("message", "이메일 인증이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "잘못된 인증번호이거나 만료되었습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
}
