package com.comwith.fitlog.init.repository;

import com.comwith.fitlog.init.entity.UserWorkoutFrequency;
import com.comwith.fitlog.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWorkoutFrequencyRepository extends JpaRepository<UserWorkoutFrequency, Long> {
    Optional<UserWorkoutFrequency> findByUser(User user);
}