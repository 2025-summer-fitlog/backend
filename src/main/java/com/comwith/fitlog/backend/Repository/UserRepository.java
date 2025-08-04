package com.comwith.fitlog.backend.Repository;

import com.comwith.fitlog.backend.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository("backendUserRepository")
public interface UserRepository extends JpaRepository<UserEntity, Long> {}

