package com.comwith.fitlog.init.service;

import com.comwith.fitlog.init.dto.*;
import com.comwith.fitlog.init.entity.*;
import com.comwith.fitlog.init.repository.*;
import com.comwith.fitlog.users.entity.User;
import com.comwith.fitlog.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InitService {

    private final UserPhysicalInfoRepository userPhysicalInfoRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserPreferredTimeRepository userPreferredTimeRepository;
    private final UserWorkoutFrequencyRepository userWorkoutFrequencyRepository;
    private final UserMainPartRepository userMainPartRepository;
    private final UserRepository userRepository; // User 엔티티를 찾기 위해 필요

    public InitService(UserPhysicalInfoRepository userPhysicalInfoRepository,
                       UserGoalRepository userGoalRepository,
                       UserPreferredTimeRepository userPreferredTimeRepository,
                       UserWorkoutFrequencyRepository userWorkoutFrequencyRepository,
                       UserMainPartRepository userMainPartRepository,
                       UserRepository userRepository) {
        this.userPhysicalInfoRepository = userPhysicalInfoRepository;
        this.userGoalRepository = userGoalRepository;
        this.userPreferredTimeRepository = userPreferredTimeRepository;
        this.userWorkoutFrequencyRepository = userWorkoutFrequencyRepository;
        this.userMainPartRepository = userMainPartRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserPhysicalInfo savePhysicalInfo(Long userId, PhysicalInfoRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 이미 신체 정보가 존재하는 경우 업데이트, 없으면 새로 생성
        UserPhysicalInfo existingInfo = userPhysicalInfoRepository.findByUser(user).orElse(null);

        if (existingInfo != null) {
            existingInfo.setHeight(dto.getHeight());
            existingInfo.setWeight(dto.getWeight());
            existingInfo.setAge(dto.getAge());
            existingInfo.setGender(dto.getGender());
            existingInfo.setWorkoutExperience(dto.getWorkoutExperience());
            return userPhysicalInfoRepository.save(existingInfo);
        } else {
            UserPhysicalInfo physicalInfo = new UserPhysicalInfo(
                    user,
                    dto.getHeight(),
                    dto.getWeight(),
                    dto.getAge(),
                    dto.getGender(),
                    dto.getWorkoutExperience()
            );
            return userPhysicalInfoRepository.save(physicalInfo);
        }
    }

    // 운동 목적
    @Transactional
    public UserGoal saveGoals(Long userId, GoalRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 기존 목표 삭제 후 새로 저장
        userGoalRepository.deleteByUser(user);
        userGoalRepository.flush(); // 즉시 변경사항을 DB에 반영

        UserGoal userGoal = new UserGoal(user, dto.getGoals()); // getGoals() → getGoal()
        return userGoalRepository.save(userGoal); // saveAll() → save()
    }

    // 희망 운동 시간
    @Transactional
    public UserPreferredTime savePreferredTime(Long userId, PreferredTimeRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        UserPreferredTime existingTime = userPreferredTimeRepository.findByUser(user).orElse(null);

        if (existingTime != null) {
            existingTime.setHours(dto.getHours());
            existingTime.setMinutes(dto.getMinutes());
            return userPreferredTimeRepository.save(existingTime);
        } else {
            UserPreferredTime preferredTime = new UserPreferredTime(user, dto.getHours(), dto.getMinutes());
            return userPreferredTimeRepository.save(preferredTime);
        }
    }


    // 운동 횟수 설정
    @Transactional
    public UserWorkoutFrequency saveWorkoutFrequency(Long userId, WorkoutFrequencyRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        UserWorkoutFrequency existingFrequency = userWorkoutFrequencyRepository.findByUser(user).orElse(null);

        if (existingFrequency != null) {
            existingFrequency.setFrequency(dto.getFrequency());
            return userWorkoutFrequencyRepository.save(existingFrequency);
        } else {
            UserWorkoutFrequency workoutFrequency = new UserWorkoutFrequency(user, dto.getFrequency());
            return userWorkoutFrequencyRepository.save(workoutFrequency);
        }
    }


    // 주요 운동 부위 설정
    @Transactional
    public List<UserMainPart> saveMainParts(Long userId, MainPartRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 기존 주요 부위 삭제 후 새로 저장
        userMainPartRepository.deleteByUser(user);
        userMainPartRepository.flush(); // 즉시 변경사항을 DB에 반영

        List<UserMainPart> savedParts = dto.getMainParts().stream()
                .map(partType -> new UserMainPart(user, partType))
                .collect(Collectors.toList());
        return userMainPartRepository.saveAll(savedParts);
    }
}