package com.comwith.fitlog.users.controller;

import com.comwith.fitlog.users.dto.UserLoginRequest;
import com.comwith.fitlog.users.dto.UserLoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.comwith.fitlog.users.service.UserService;
import com.comwith.fitlog.users.dto.UserRegisterRequest;
import com.comwith.fitlog.users.entity.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    /*
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
        UserLoginResponse response = new UserLoginResponse(loginUser.getId(), loginUser.getUsername(), loginUser.getName());
        return ResponseEntity.ok(response);
    }

     */

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        User saved = userService.save(request);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body((UserLoginResponse) errors);
        }
        User loginUser = userService.login(request.getUsername(), request.getPassword());
        UserLoginResponse response = new UserLoginResponse(loginUser.getId(), loginUser.getUsername(), loginUser.getName()); // loginUser.getNickname() -> loginUser.getName()
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }



}
