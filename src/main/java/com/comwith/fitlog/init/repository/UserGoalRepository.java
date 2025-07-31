package com.comwith.fitlog.init.repository;

import com.comwith.fitlog.init.entity.UserGoal;
import com.comwith.fitlog.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    List<UserGoal> findByUser(User user);
    void deleteByUser(User user); // 기존 목표 삭제를 위해 필요
}