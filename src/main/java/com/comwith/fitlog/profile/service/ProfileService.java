package com.comwith.fitlog.profile.service;

import com.comwith.fitlog.init.dto.PhysicalInfoRequest;
import com.comwith.fitlog.init.dto.PreferredTimeRequest;
import com.comwith.fitlog.init.dto.WorkoutFrequencyRequest;
import com.comwith.fitlog.init.entity.*;
import com.comwith.fitlog.init.repository.*;
import com.comwith.fitlog.profile.dto.ProfilePersonalInfoRequest;
import com.comwith.fitlog.profile.dto.ProfilePersonalInfoResponse;
import com.comwith.fitlog.profile.dto.ProfileUserResponse;
import com.comwith.fitlog.profile.dto.WorkoutInfoResponseDto;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    // 개인 계정정보 관련
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 개인 운동정보 관련
    private final UserPhysicalInfoRepository physicalInfoRepository;
    private final UserGoalRepository goalRepository;
    private final UserPreferredTimeRepository preferredTimeRepository;
    private final UserWorkoutFrequencyRepository frequencyRepository;
    private final UserMainPartRepository mainPartRepository;

    public ProfileUserResponse getCurrentUserProfile(String username) {
        log.info("사용자 프로필 조회 요청: {}", username);

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명이 비어있습니다.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다 :" + username));

        return ProfileUserResponse.builder()
                .name(user.getName())
                .build();
    }


    // 소셜/로컬 로그인 통합 사용자 조회
    public User findUserByAuthentication(Authentication authentication) {
        String identifier = authentication.getName();
        log.info("사용자 조회 시도 - identifier: '{}'", identifier);

        // 1차: username으로 조회 (로컬 가입 사용자)
        if (identifier != null && !identifier.isEmpty()) {
            Optional<User> userOpt = userRepository.findByUsername(identifier);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }

        // 2차: email로 조회 (소셜 로그인 사용자)
        if (identifier != null && identifier.contains("@")) {
            Optional<User> userOpt = userRepository.findByEmail(identifier);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }

        // 3차: loginMethod와 provider_id로 조회 (소셜로그인용 카카오/구글 로그인)
        if (identifier != null && !identifier.isEmpty() && !identifier.contains("@")) {
            // 카카오 로그인 시도
            Optional<User> userOpt = userRepository.findByLoginMethodAndProviderId("KAKAO", identifier);
            if (userOpt.isPresent()) {
                log.info("KAKAO provider_id로 사용자 찾음: '{}'", identifier);
                return userOpt.get();
            }

            // 구글 로그인 시도
            userOpt = userRepository.findByLoginMethodAndProviderId("GOOGLE", identifier);
            if (userOpt.isPresent()) {
                log.info("GOOGLE provider_id로 사용자 찾음: '{}'", identifier);
                return userOpt.get();
            }
        }

        throw new RuntimeException("사용자를 찾을 수 없습니다: " + identifier);
    }

    // 개인정보 조회
    public ProfilePersonalInfoResponse getPersonalInfo(Authentication authentication) {
        User user = findUserByAuthentication(authentication);

        // 소셜 로그인 여부 체크
        boolean isSocialLogin = user.getLoginMethod() != null &&
                !user.getLoginMethod().equals("LOCAL");


        String passwordInfo = null;
        if (!isSocialLogin) {
            passwordInfo = "*".repeat(10);
            // 비밀번호 마스킹처리 길이 (원래 비밀번호 길이만큼 해서 length로 하려했으나,
            // 암호화처리되서 너무 길어져서 일단 숫자로 대체)
        }

        return ProfilePersonalInfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                // .username(user.getUsername())
                .password(passwordInfo)
                .loginMethod(user.getLoginMethod())
                .isChangePassword(!isSocialLogin)
                .build();
    }

    // 개인정보 수정
    public ProfilePersonalInfoResponse updatePersonalInfo(Authentication authentication, ProfilePersonalInfoRequest request) {
        User user = findUserByAuthentication(authentication);

        // 소셜 로그인 여부 체크
        boolean isSocialLogin = user.getLoginMethod() != null &&
                !user.getLoginMethod().equals("LOCAL");

        // 개인정보 업데이트
        // 이름
        user.setName(request.getName());
        // user.setEmail(request.getEmail());

        // 비밀번호
        if (!isSocialLogin && request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        } else if (isSocialLogin && request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }


        User savedUser = userRepository.save(user);
        log.info("개인정보 수정 완료!");

        // 응답 생성
        String passwordInfo = null;
        if (!isSocialLogin) {
            passwordInfo = "*".repeat(10);
        }

        return ProfilePersonalInfoResponse.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                // .username(savedUser.getUsername())
                .password(passwordInfo)
                .loginMethod(savedUser.getLoginMethod())
                .isChangePassword(!isSocialLogin)
                .build();
    }


    /* ---------------------- 운동 정보 조회 및 수정 관련 메소드 --------------------------- */

    public WorkoutInfoResponseDto getWorkoutInfo(Authentication auth) {
        String identifier = auth.getName();
        log.info("운동정보 조회 요청 - identifier: {}", identifier);

        User user = userRepository.findByUsernameOrProviderId(identifier)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + identifier));

        WorkoutInfoResponseDto response = WorkoutInfoResponseDto.builder()
                .physicalInfo(getPhysicalInfo(user))
                .goals(getGoals(user))
                .preferredTime(getPreferredTime(user))
                .frequency(getFrequency(user))
                .mainParts(getMainParts(user))
                .build();

        log.info("운동정보 조회 완료 - 사용자: {}", user.getUsername());
        return response;
    }

    // Entity → 기존 Request DTO 변환 (간단!)
    private PhysicalInfoRequest getPhysicalInfo(User user) {
        UserPhysicalInfo entity = physicalInfoRepository.findByUser(user).orElse(null);
        if (entity == null) return null;

        PhysicalInfoRequest dto = new PhysicalInfoRequest();
        dto.setHeight(entity.getHeight());
        dto.setWeight(entity.getWeight());
        dto.setAge(entity.getAge());
        dto.setGender(entity.getGender());
        dto.setWorkoutExperience(entity.getWorkoutExperience());
        return dto;
    }

    // goalType만 추출해서 String 리스트로
    private String getGoals(User user) {
        return goalRepository.findByUser(user).stream()
                .map(UserGoal::getGoalType)  // Entity 필드명: goalType
                .findFirst()
                // .collect(Collectors.toList());
                .orElse(null);
    }

    // Entity → 기존 Request DTO 변환
    private PreferredTimeRequest getPreferredTime(User user) {
        UserPreferredTime entity = preferredTimeRepository.findByUser(user).orElse(null);
        if (entity == null) return null;

        PreferredTimeRequest dto = new PreferredTimeRequest();
        dto.setHours(entity.getHours());
        dto.setMinutes(entity.getMinutes());
        return dto;
    }

    // Entity → 기존 Request DTO 변환
    private WorkoutFrequencyRequest getFrequency(User user) {
        UserWorkoutFrequency entity = frequencyRepository.findByUser(user).orElse(null);
        if (entity == null) return null;

        WorkoutFrequencyRequest dto = new WorkoutFrequencyRequest();
        dto.setFrequency(entity.getFrequency());
        return dto;
    }

    // partType만 추출해서 String 리스트로
    private List<String> getMainParts(User user) {
        return mainPartRepository.findByUser(user).stream()
                .map(UserMainPart::getPartType)  // Entity 필드명: partType
                .collect(Collectors.toList());
    }

    public User findUserByUsernameOrProviderId(String identifier) {
        return userRepository.findByUsernameOrProviderId(identifier)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + identifier));
    }

}
