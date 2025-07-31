package com.comwith.fitlog.init.repository;

import com.comwith.fitlog.init.entity.UserPreferredTime;
import com.comwith.fitlog.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferredTimeRepository extends JpaRepository<UserPreferredTime, Long> {
    Optional<UserPreferredTime> findByUser(User user);
}