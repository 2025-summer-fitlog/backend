package com.comwith.fitlog.init.repository;

import com.comwith.fitlog.init.entity.UserMainPart;
import com.comwith.fitlog.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMainPartRepository extends JpaRepository<UserMainPart, Long> {
    List<UserMainPart> findByUser(User user);
    void deleteByUser(User user); // 기존 부위 삭제를 위해 필요
}
