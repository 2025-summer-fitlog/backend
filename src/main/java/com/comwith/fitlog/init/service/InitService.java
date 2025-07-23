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
//    private final UserPreferredTimeRepository userPreferredTimeRepository;
//    private final UserWorkoutFrequencyRepository userWorkoutFrequencyRepository;
//    private final UserMainPartRepository userMainPartRepository;
    private final UserRepository userRepository; // User 엔티티를 찾기 위해 필요

    public InitService(UserPhysicalInfoRepository userPhysicalInfoRepository,
                       UserGoalRepository userGoalRepository,
//                       UserPreferredTimeRepository userPreferredTimeRepository,
//                       UserWorkoutFrequencyRepository userWorkoutFrequencyRepository,
//                       UserMainPartRepository userMainPartRepository,
                       UserRepository userRepository) {
        this.userPhysicalInfoRepository = userPhysicalInfoRepository;
        this.userGoalRepository = userGoalRepository;
//        this.userPreferredTimeRepository = userPreferredTimeRepository;
//        this.userWorkoutFrequencyRepository = userWorkoutFrequencyRepository;
//        this.userMainPartRepository = userMainPartRepository;
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

    // 다른 초기화 정보 저장 메서드들도 여기에 추가됩니다.
    // ...

    // ... (기존 코드)

    @Transactional
    public List<UserGoal> saveGoals(Long userId, GoalRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 기존 목표 삭제 후 새로 저장
        userGoalRepository.deleteByUser(user);
        userGoalRepository.flush(); // 즉시 변경사항을 DB에 반영

        List<UserGoal> savedGoals = dto.getGoals().stream()
                .map(goalType -> new UserGoal(user, goalType))
                .collect(Collectors.toList());
        return userGoalRepository.saveAll(savedGoals);
    }
}