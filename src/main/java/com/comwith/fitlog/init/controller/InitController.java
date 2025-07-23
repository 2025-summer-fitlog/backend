package com.comwith.fitlog.init.controller;

import com.comwith.fitlog.init.dto.GoalRequest;
import com.comwith.fitlog.init.dto.PhysicalInfoRequest;
import com.comwith.fitlog.init.entity.UserGoal;
import com.comwith.fitlog.init.entity.UserPhysicalInfo;
import com.comwith.fitlog.init.service.InitService;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    // 신체 정보 수집
    @PostMapping("/body")
    public ResponseEntity<?> collectPhysicalInfo(@Valid @RequestBody PhysicalInfoRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // 현재 로그인된 사용자의 ID를 가져오는 로직 (Spring Security를 사용한다고 가정)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자만 접근할 수 있습니다.");
        }
        // UserDetails에서 사용자 ID를 추출하거나, User 엔티티에 직접 ID를 가져오는 메서드를 추가해야 합니다.
        // 여기서는 예시로 사용자 이름을 통해 User 엔티티를 찾아 ID를 얻는다고 가정합니다.
        // 실제로는 CustomUserDetails를 사용하거나, JWT 토큰 등에서 userId를 추출하는 방식이 더 일반적입니다.
        // 현재 UserService의 loadUserByUsername이 UserDetails를 반환하므로, 이를 활용합니다.
        // Long userId = ((com.comwith.fitlog.users.entity.User) authentication.getPrincipal()).getId();

        // authentication.getPrincipal()이 String 또는 UserDetails 객체를 반환할 수 있으므로 안전하게 처리
        String username;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal; // "anonymousUser" 또는 실제 사용자 이름일 수 있음
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보를 가져올 수 없습니다.");
        }

        // 사용자 이름으로 User 엔티티를 조회하여 ID를 가져옵니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다: " + username));
        Long userId = user.getId();
        // --- 수정된 부분 끝 ---


        try {
            UserPhysicalInfo savedInfo = initService.savePhysicalInfo(userId, request);
            return ResponseEntity.ok(savedInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자만 접근할 수 있습니다.");
        }
        Long userId = ((com.comwith.fitlog.users.entity.User) authentication.getPrincipal()).getId();

        try {
            List<UserGoal> savedGoals = initService.saveGoals(userId, request);
            return ResponseEntity.ok(savedGoals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), ex.getMessage()));
        return errors;
    }
}