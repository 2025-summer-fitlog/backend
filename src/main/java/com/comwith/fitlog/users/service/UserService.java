package com.comwith.fitlog.users.service;

import com.comwith.fitlog.users.dto.UserRegisterRequest;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User save(UserRegisterRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        User entity = new User();
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        entity.setName(dto.getName()); // dto.getNickname() -> dto.getName()
        entity.setLoginMethod("LOCAL");
        return userRepository.save(entity);
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!bCryptPasswordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다.");
        }
        return user;
    }


    /**
     * Spring Security의 UserDetailsService 인터페이스 구현 메서드.
     * 사용자 이름을 기반으로 사용자 정보를 로드합니다.
     *
     * @param username 로그인 시도하는 사용자 아이디
     * @return Spring Security의 UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UserRepository를 사용하여 데이터베이스에서 사용자 정보를 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("아이디를 찾을 수 없습니다: " + username));

        // 조회된 사용자 정보를 Spring Security의 UserDetails 객체로 변환하여 반환
        // 여기서는 간단하게 "ROLE_USER" 권한을 부여합니다.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // 사용자의 아이디 (인증 주체)
                user.getPassword(), // 암호화된 비밀번호
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 사용자에게 부여할 권한 목록
        );
    }
}