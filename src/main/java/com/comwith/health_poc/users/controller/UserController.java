package com.comwith.health_poc.users.controller;

import com.comwith.health_poc.users.dto.UserLoginRequest;
import com.comwith.health_poc.users.dto.UserLoginResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.comwith.health_poc.users.service.UserService;
import com.comwith.health_poc.users.dto.UserRegisterRequest;
import com.comwith.health_poc.users.entity.User;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 정보 입력(저장)
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegisterRequest request) {
        User saved = userService.save(request);
        return ResponseEntity.ok(saved);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        User loginUser = userService.login(request.getUsername(), request.getPassword());
        // 실제 서비스에서는 세션/토큰 반환 권장
        UserLoginResponse response = new UserLoginResponse(loginUser.getId(), loginUser.getUsername(), loginUser.getNickname());
        return ResponseEntity.ok(response);
    }

}
