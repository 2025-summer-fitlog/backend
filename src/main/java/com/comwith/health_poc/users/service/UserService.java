package com.comwith.health_poc.users.service;

import com.comwith.health_poc.users.dto.UserRegisterRequest;
import com.comwith.health_poc.users.entity.User;
import com.comwith.health_poc.users.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(UserRegisterRequest dto) {
        User entity = new User();
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword()); // 반드시 암호화해서 저장 추천!
        entity.setNickname(dto.getNickname());
        return userRepository.save(entity);
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        // 암호화된 패스워드 비교 필수!
        if (!user.getPassword().equals(rawPassword)) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다.");
        }
        return user;
    }
}
